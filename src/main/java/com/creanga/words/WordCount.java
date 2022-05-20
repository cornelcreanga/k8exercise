package com.creanga.words;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.File;
import java.util.*;

public class WordCount {

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("WordCount example");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

//        String limitEnv = System.getenv("ITERATIONS");
//        int limit = limitEnv == null ? 1 : Integer.parseInt(limitEnv);
        System.out.println("------Arguments------");
        for (String arg : args) {
            System.out.println(arg);
        }
        System.out.println("SECRET_USERNAME:"+System.getenv("SECRET_USERNAME"));
        System.out.println("SECRET_PASSWORD:"+System.getenv("SECRET_PASSWORD"));
        File file = new File("/opt/spark/mycm");
        if (file.exists()) {
            System.out.println("/opt/spark/mycm content");
            String[] files = file.list();
            if (files != null) {
                for (String s : files) {
                    System.out.println(s);
                }
            }
        } else {
            System.out.println("/opt/spark/mycm missing");
        }

        int limit = 10;
        System.out.println("Iterations " + limit);

        for (int c = 0; c < limit; c++) {
            System.out.println("Iteration " + (c + 1));
            List<String> wordList = new ArrayList<>();
            for (int i = 0; i < 1000000; i++) {
                wordList.add(RandomStringUtils.randomAlphabetic(4));
            }

            JavaRDD<String> words = sparkContext.parallelize(wordList, 8);

            JavaPairRDD<String, Integer> pairs = words.mapToPair(s -> new Tuple2<>(s, 1));

            JavaPairRDD<String, Integer> counts = pairs.reduceByKey(Integer::sum);

            Map<String, Integer> map = sortByValue(counts.collectAsMap());
            Set<String> keys = map.keySet();
            int counter = 0;
            for (String next : keys) {
                if (++counter > 5)
                    break;
                System.out.println(next + " : " + map.get(next));
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }

        sparkContext.stop();
        sparkContext.close();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}