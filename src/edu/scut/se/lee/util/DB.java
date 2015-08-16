package edu.scut.se.lee.util;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

import java.util.List;

import edu.scut.se.lee.App;

/**
 * Created by jsonlee on 8/16/15.
 */
public class DB {
    private static FinalDb db = null;
    private static FinalDb getDb(){
        if(db == null)
            db = FinalDb.create(App.getInstance());
        return db;
    }
    public static void putResult(Result result){
        getDb().save(result);
    }
    public static List<Result> getResults(){
        return getDb().findAll(Result.class);
    }

    @Table(name = "Result")
    public static class Result {
        @Id
        private String name;
        private double frequency;
        private double result1;
        private double result2;

        public Result(){
        }
        public Result(String name, double frequency, double result1, double result2) {
            this.name = name;
            this.frequency = frequency;
            this.result1 = result1;
            this.result2 = result2;
        }

        public double getFrequency() {
            return frequency;
        }

        public void setFrequency(double frequency) {
            this.frequency = frequency;
        }

        public double getResult1() {
            return result1;
        }

        public void setResult1(double result1) {
            this.result1 = result1;
        }

        public double getResult2() {
            return result2;
        }

        public void setResult2(double result2) {
            this.result2 = result2;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
