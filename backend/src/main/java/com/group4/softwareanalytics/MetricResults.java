package com.group4.softwareanalytics;

import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKNotifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MetricResults implements CKNotifier {
    private float averageCBO = 0F;
    private float averageWMC = 0F;
    private float averageLCOM = 0F;
    private float averageLOC = 0F;
    private int numberOfClasses = 0;

    private Set<String> javaFiles = new HashSet<>();

    @Override
    public void notify(CKClassResult ckClassResult) {
        System.out.println(ckClassResult.getFile());
        // TODO: add control if file list is empty
//        if (javaFiles.contains(ckClassResult.getFile())){
//            System.out.println("file counted");
            this.numberOfClasses = this.numberOfClasses + 1;
            this.averageCBO += ckClassResult.getCbo();
            this.averageLCOM += ckClassResult.getLcom();
            this.averageWMC += ckClassResult.getWmc();
            this.averageLOC += ckClassResult.getLoc();
//        }
    }

    public ArrayList<Float> getResults(){
        if (numberOfClasses > 0){
            averageCBO = averageCBO / numberOfClasses;
            averageWMC = averageWMC / numberOfClasses;
            averageLCOM = averageLCOM / numberOfClasses;
            averageLOC = averageLOC / numberOfClasses;
        }
        return new ArrayList<Float>(Arrays.asList(averageCBO,averageWMC,averageLCOM,averageLOC));
    }

    public Set<String> getJavaFiles() {
        return javaFiles;
    }

    public void setJavaFiles(Set<String> javaFiles) {
        this.javaFiles = javaFiles;
    }
}
