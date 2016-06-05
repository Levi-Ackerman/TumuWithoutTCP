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
        private double force1;
        private double force2;

        public double getForce3() {
            return force3;
        }

        public void setForce3(double force3) {
            this.force3 = force3;
        }

        private double force3;

        public Result(){
        }

        public Result(String name, double length, double force1, double force2, double force3) {
            this.name = name;
            this.length = length;
            this.force1 = force1;
            this.force2 = force2;
            this.force3 = force3;
            this.prjName = Cache.getInstance().load(Cache.PRJ_NAME,"");
        }

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        public double getForce1() {
            return force1;
        }

        public void setForce1(double force1) {
            this.force1 = force1;
        }

        public double getForce2() {
            return force2;
        }

        public void setForce2(double force2) {
            this.force2 = force2;
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
