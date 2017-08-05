package com.dzondza.vasya.diagnostix.MainContent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dzondza.vasya.diagnostix.RecyclerItemsData;
import com.dzondza.vasya.diagnostix.R;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * contains information about system, cpu
 */

public class SystemFragment extends BaseDetailedFragment {
    private Handler handler = new Handler();
    private Runnable coreFrequencyRunnable;
    private int cpuNumber;
    private String supportedAbis;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_recyclerview, container, false);

        initializeRecyclerView(view);


        Runtime runtime = Runtime.getRuntime();
        cpuNumber = runtime.availableProcessors();
        recyclerViewLine.add(new RecyclerItemsData(getString(R.string.system_cores), String.valueOf(cpuNumber)));


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            supportedAbis = new StringBuilder(Build.CPU_ABI).append(" ").append(Build.CPU_ABI2).toString();
        } else {
            for (String s : Build.SUPPORTED_ABIS) {
                    supportedAbis += s + " ";
            }
        }
        supportedAbis = supportedAbis.replaceAll("null","");
        recyclerViewLine.add(new RecyclerItemsData("Instruction Set", supportedAbis));



        List<Integer> maxCpuFreqList = new ArrayList<>(cpuNumber);

        for (int i = 0; i < cpuNumber; i++) {
            //each core frequency
            int coreFrequency = readIntegerFile("/sys/devices/system/cpu/cpu" + i +
            "/cpufreq/scaling_cur_freq") / 1000;
            recyclerViewLine.add(2 + i, new RecyclerItemsData("Core " + i, String.valueOf(coreFrequency)
                    .concat(" MHz")));


            int maxCpuFreq = readIntegerFile("/sys/devices/system/cpu/cpu"+i+"/cpufreq/cpuinfo_max_freq")/1000;
            maxCpuFreqList.add(maxCpuFreq);
        }

        //Clock Speed
        String clockSpeedmin = Collections.min(maxCpuFreqList).toString();
        String clockSpeedmax = Collections.max(maxCpuFreqList).toString();
        recyclerViewLine.add(new RecyclerItemsData("Clock Speed", new StringBuilder(clockSpeedmin)
                .append(" MHz - ").append(clockSpeedmax).append(" MHz").toString()));


        String osArchitecture = getString(R.string.system_os_architect);
        recyclerViewLine.add(new RecyclerItemsData(osArchitecture, System.getProperty("os.arch")));


        String kernelDescript = getString(R.string.system_kernel);
        String kernel = new StringBuilder(System.getProperty("os.name", "")).append(" ")
                .append(System.getProperty("os.version", "")).toString();
        recyclerViewLine.add(new RecyclerItemsData(kernelDescript, kernel));


        String vmLocation = getString(R.string.system_vm_location);
        recyclerViewLine.add(new RecyclerItemsData(vmLocation, System.getProperty("java.home",
                getString(R.string.unknown))));

        String jniLibraries = getString(R.string.system_jni_libraries);
        recyclerViewLine.add(new RecyclerItemsData(jniLibraries, System.getProperty("java.library.path",
                getString(R.string.unknown))));


        String virtualMachineDescript = getString(R.string.system_virtual_machine);
        String virtualMachine = new StringBuilder(System.getProperty("java.vm.name", ""))
                .append(" Vendor: ").append(System.getProperty("java.vm.vendor", ""))
                .append(" Version: ").append(System.getProperty("java.vm.version", "")).toString();
        recyclerViewLine.add(new RecyclerItemsData(virtualMachineDescript, virtualMachine));


        String vmLibrariesDescript = getString(R.string.system_vm_libraries);
        String vmLibraries = new StringBuilder(System.getProperty("java.specification.name", ""))
                .append(" Vendor: ").append(System.getProperty("java.specification.vendor", ""))
                .append(" Version: ").append(System.getProperty("java.specification.version", ""))
                .toString();
        recyclerViewLine.add(new RecyclerItemsData(vmLibrariesDescript, vmLibraries));


        String bootloader = getString(R.string.system_bootloader);
        recyclerViewLine.add(new RecyclerItemsData(bootloader, Build.BOOTLOADER));

        String host = getString(R.string.system_host);
        recyclerViewLine.add(new RecyclerItemsData(host, Build.HOST));


        //toolbar title
        getActivity().setTitle(R.string.drawer_system);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        coreFrequencyRunnable = new Runnable() {
            @Override
            public void run() {

                // refresh each core frequency
                for (int i = 0; i < cpuNumber; i++) {
                    int coreFrequency = readIntegerFile("/sys/devices/system/cpu/cpu" + i +
                            "/cpufreq/scaling_cur_freq") / 1000;
                    recyclerViewLine.set(i + 2, new RecyclerItemsData("Core " + i, String.valueOf(coreFrequency)
                            .concat(" MHz")));
                    adapter.notifyDataSetChanged();
                }
                handler.postDelayed(this, 5);
            }
        };
        handler.postDelayed(coreFrequencyRunnable, 100);
    }


    //gets integer value from file
    private static int readIntegerFile(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath)), 1000);
            String line = reader.readLine();
            reader.close();

            return Integer.parseInt(line);
        }catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    @Override
    public void onPause() {
        handler.removeCallbacks(coreFrequencyRunnable);
        super.onPause();
    }
}