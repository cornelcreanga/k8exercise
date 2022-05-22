package com.creanga.words;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComputePi {

    private static double calcPiSlice(long start, long end) {

        double pi = 0;
        long i = start;
        while (i < end) {
            if (i % 2 == 0) {
                pi += -1d / (2 * i - 1);
            } else {
                pi += 1d / (2 * i - 1);
            }
            i++;
        }
        return pi;
    }

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("Pi example");
        //sparkConf.setMaster("local[4]");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        LogManager.getRootLogger().setLevel(Level.WARN);
        Logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
        Logger.getLogger("org.apache.hadoop").setLevel(Level.ERROR);

        Logger.getLogger("com.codahale.metrics.graphite").setLevel(Level.OFF);


        sparkContext.parallelize(new ArrayList<>()).foreachPartition(objectIterator -> {
                    LogManager.getRootLogger().setLevel(Level.WARN);
                    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
                    Logger.getLogger("com.codahale.metrics.graphite").setLevel(Level.OFF);
                }
        );

        int partitions = 8;
        long slice = 10_000_000_000L / partitions;

        List<Integer> partitionList = new ArrayList<>(partitions);
        for (int i = 0; i < partitions; i++) {
            partitionList.add(i);
        }
        JavaRDD<Integer> list = sparkContext.parallelize(partitionList, partitions);

        for (int i = 0; i < 10; i++) {
            long t1 = System.currentTimeMillis();
            double pi = computePi(slice, list);
            long t2 = System.currentTimeMillis();
            System.out.println("time: " + (t2 - t1) + " ms");
            System.out.println(pi);
        }

    }

    private static double computePi(long slice, JavaRDD<Integer> list) {
        List<Double> piSlices = list.mapPartitions(integerIterator -> {
            int no = TaskContext.getPartitionId();
            long start = (no == 0) ? 1 : no * slice;

            double pi = calcPiSlice(start, (no + 1) * slice);
            return Collections.singleton(pi).iterator();
        }).collect();
        double pi = 0;
        for (Double piSlice : piSlices) {
            pi += piSlice;
        }
        pi = pi * 4;
        return pi;
    }
}
