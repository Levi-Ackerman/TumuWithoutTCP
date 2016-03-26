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

    public static void deleteResult(long dbId) {
        getDb().deleteById(Result.class,dbId);
    }

    @Table(name = "Result")
    public static class Result {
        private String prjName;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        @Id
        private long id;
        private String name;
        private double length;
        private double midu;
        private double freq;

        public double getForce() {
            return force;
        }

        public void setForce(double force) {
            this.force = force;
        }

        private double force;

        public Result(){
        }

        public Result(String name, double length, double midu, double freq, double force) {
            this.name = name;
            this.length = length;
            this.midu = midu;
            this.freq = freq;
            this.force = force;
            this.prjName = Cache.getInstance().load(Cache.PRJ_NAME,"");
        }

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        public double getMidu() {
            return midu;
        }

        public void setMidu(double midu) {
            this.midu = midu;
        }

        public double getFreq() {
            return freq;
        }

        public void setFreq(double freq) {
            this.freq = freq;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrjName() {
            return prjName;
        }

        public void setPrjName(String prjName) {
            this.prjName = prjName;
        }
    }
}
