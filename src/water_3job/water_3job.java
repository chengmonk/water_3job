/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package water_3job;

import Automation.BDaq.InstantAiCtrl;
import Automation.BDaq.InstantAoCtrl;
import Automation.BDaq.InstantDiCtrl;
import Automation.BDaq.InstantDoCtrl;
import Automation.BDaq.*;
import Common.Global;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

/**
 *
 * @author Mr.C
 */
class ConfigureParameter {

    public String deviceName;
    public int channelStart;
    public int channelCount;
    public String profile;

    public void initial(String deviceName, int channelStart, int channelCount, String profile) {
        this.deviceName = deviceName;
        this.channelCount = channelCount;
        this.channelStart = channelStart;
        this.profile = profile;
    }
}

public class water_3job extends javax.swing.JFrame {

    ConfigureParameter configure = new ConfigureParameter();
    double max_job1 = 0;
    double max_job2 = 0;
    double max_job3 = 0;
    double adjust0;
    double adjust1;
    double adjust2;
    double adjust3;
    double adjust4;
    double adjust5;
    double adjust6;

    private InstantAiCtrl instantAiCtrl = new InstantAiCtrl();
    private double[] AIData = new double[16];
    private double[] frequencydata = new double[30];
    int pose = 0;
    private InstantAoCtrl instantAoCtrl = new InstantAoCtrl();
    private double[] AOData = new double[2];
    private InstantDiCtrl instantDiCtrl = new InstantDiCtrl();
    private byte[] DIData = new byte[2];
    private InstantDoCtrl instantDoCtrl = new InstantDoCtrl();
    private byte[] DOData = new byte[2];
    private String lastTesttime;
    private String lastMaxpush;
    private String lastTestcount;
    private String lastKeeptime;
    boolean flag = false;

    Timer AItimer;
    Timer DItimer;
    DOdataSubmit_job1_auto dosub_job1;
    DOdataSubmit_job2_auto dosub_job2;
    DOdataSubmit_job3_auto dosub_job3;

    double avg() {
        double sum = 0;
        for (int i = 0; i < 30; i++) {
            sum += frequencydata[i];
        }
        sum = sum / 30;
        sum = (double) Math.round(sum * 100) / 100;
        return sum;
    }

    void write_adjust() {

        String filePath = "D:\\adjust.csv";

        try {
            // 创建CSV写对象
            CsvWriter csvWriter = new CsvWriter(filePath, ',', Charset.forName("UTF-8"));
            // 写表头
            String[] headers = {"adjust0", "adjust1", "adjust2", "adjust3", "adjust4","pump"};
            String[] content = {push_adjust1.getText(), push_adjust2.getText(),
                push_adjust3.getText(), waterpressure_adjust.getText(), water_adjust.getText(),pumpPressure.getText()};
            //System.out.print(T_job1.getText());
            csvWriter.writeRecord(headers);
            csvWriter.writeRecord(content);
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void read_adjust() {
        String filePath = "D:\\adjust.csv";
        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath);

            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()) {
//                // 读一整行
//                System.out.println(csvReader.getRawRecord());
//                // 读这行的某一列
//                System.out.println(csvReader.get("Link"));
                //T_job1.setText(csvReader.get("circleTime"));
                //System.out.print(T_job1.getText() + "@@@");

                push_adjust1.setText(csvReader.get("adjust0"));
                push_adjust2.setText(csvReader.get("adjust1"));
                push_adjust3.setText(csvReader.get("adjust2"));
                waterpressure_adjust.setText(csvReader.get("adjust3"));
                water_adjust.setText(csvReader.get("adjust4"));
                pumpPressure.setText(csvReader.get("pump"));
                adjust0 = Double.parseDouble(csvReader.get("adjust0"));
                adjust1 = Double.parseDouble(csvReader.get("adjust1"));
                adjust2 = Double.parseDouble(csvReader.get("adjust2"));
                adjust3 = Double.parseDouble(csvReader.get("adjust3"));
                adjust4 = Double.parseDouble(csvReader.get("adjust4"));

            }
            csvReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void write_job1() {

        String filePath = "D:\\config1.csv";

        try {
            // 创建CSV写对象
            CsvWriter csvWriter = new CsvWriter(filePath, ',', Charset.forName("UTF-8"));
            // 写表头
            String[] headers = {"currentCount", "maxpush_top","maxpush_down",
                "testtime", "testcount", "keeptime"};
            String[] content = {testedCount_job1.getText(), maxpush_job1_top.getText(),maxpush_job1_down.getText(),
                testtime_job1.getText(), testcount_job1.getText(), keeptime_job1.getText()};
            //System.out.print(T_job1.getText());
            csvWriter.writeRecord(headers);
            csvWriter.writeRecord(content);
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void write_job2() {

        String filePath = "D:\\config2.csv";

        try {
            // 创建CSV写对象
            CsvWriter csvWriter = new CsvWriter(filePath, ',', Charset.forName("UTF-8"));
            // 写表头
            String[] headers = {"currentCount", "maxpush_top","maxpush_down","testtime", "testcount", "keeptime"};
            String[] content = {testedCount_job2.getText(), maxpush_job2_top.getText(),maxpush_job2_down.getText(),
                testtime_job2.getText(), testcount_job2.getText(), keeptime_job2.getText()};
            csvWriter.writeRecord(headers);
            csvWriter.writeRecord(content);
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void write_job3() {

        String filePath = "D:\\config3.csv";

        try {
            // 创建CSV写对象
            CsvWriter csvWriter = new CsvWriter(filePath, ',', Charset.forName("UTF-8"));
            // 写表头
            String[] headers = {"currentCount", "maxpush_top","maxpush_down", "testtime", "testcount", "keeptime"};
            String[] content = {testedCount_job3.getText(), maxpush_job3_top.getText(),maxpush_job3_down.getText(),
                testtime_job3.getText(), testcount_job3.getText(), keeptime_job3.getText()};
            csvWriter.writeRecord(headers);
            csvWriter.writeRecord(content);
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void read_job1() {
        String filePath = "D:\\config1.csv";
        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath);

            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()) {
//                // 读一整行
//                System.out.println(csvReader.getRawRecord());
//                // 读这行的某一列
//                System.out.println(csvReader.get("Link"));
                //T_job1.setText(csvReader.get("circleTime"));
                //System.out.print(T_job1.getText() + "@@@");
                testedCount_job1.setText(csvReader.get("currentCount"));
                maxpush_job1_top.setText(csvReader.get("maxpush_top"));
                maxpush_job1_down.setText(csvReader.get("maxpush_down"));
                testtime_job1.setText(csvReader.get("testtime"));
                testcount_job1.setText(csvReader.get("testcount"));
                keeptime_job1.setText(csvReader.get("keeptime"));

            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void read_job2() {
        String filePath = "D:\\config2.csv";
        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath);

            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()) {
//                // 读一整行
//                System.out.println(csvReader.getRawRecord());
//                // 读这行的某一列
//                System.out.println(csvReader.get("Link"));
                //circleTimeJob2 = Integer.parseInt();
                //T_job2.setText(csvReader.get("circleTime"));
                testedCount_job2.setText(csvReader.get("currentCount"));
                maxpush_job2_top.setText(csvReader.get("maxpush_top"));
                maxpush_job2_down.setText(csvReader.get("maxpush_down"));
                testtime_job2.setText(csvReader.get("testtime"));
                testcount_job2.setText(csvReader.get("testcount"));
                //waterPressure_job2.setText(csvReader.get("waterpressure"));
                keeptime_job2.setText(csvReader.get("keeptime"));

            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void read_job3() {
        String filePath = "D:\\config3.csv";
        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath);

            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()) {
//                // 读一整行

//                // 读这行的某一列
                testedCount_job3.setText(csvReader.get("currentCount"));
                maxpush_job3_top.setText(csvReader.get("maxpush_top"));
                maxpush_job3_down.setText(csvReader.get("maxpush_down"));
                testtime_job3.setText(csvReader.get("testtime"));
                testcount_job3.setText(csvReader.get("testcount"));
                keeptime_job3.setText(csvReader.get("keeptime"));

            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class DOdataSubmit_job1_auto extends Thread {

        @Override
        public void run() {
            DOData[0] = (byte) (DOData[0] & 0xFB);
            while (true) {
                try {
                    DOData[0] = (byte) (DOData[0] ^ 0x04);
                    ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
                    if (Global.BioFaild(errorCode)) {
                        JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                    }
                    sleep((int) (Double.parseDouble(keeptime_job1.getText()) * 1000));//输出1 的时间
                    DOData[0] = (byte) (DOData[0] & 0xFB);
                    errorCode = instantDoCtrl.Write(0, DOData[0]);
                    if (Global.BioFaild(errorCode)) {
                        JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                    }
                    sleep((int) (Double.parseDouble(testtime_job1.getText()) * 1000));//输出0 的时间
                    int t = Integer.parseInt(testedCount_job1.getText());
                    if (t < Integer.parseInt(testcount_job1.getText())) {
                        t++;
                        testedCount_job1.setText(Integer.toString(t));//执行完一次 次数增1
                        write_job1();//执行完一次，将记录写入到硬盘
                    } else {
                        //自动执行停止按钮                       
                        configbut_job1.setEnabled(true);
                        stop_job1.setEnabled(false);
                        open_job1.setEnabled(true);
                        autorun_job1.setEnabled(true);
                        JOptionPane.showMessageDialog(null, "工件1测试完毕！请注意！");
                        if (dosub_job1 != null) {
                            dosub_job1.stop();
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(water_3job.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    class DOdataSubmit_job2_auto extends Thread {

        @Override
        public void run() {
            DOData[0] = (byte) (DOData[0] & 0xF7);
            while (true) {
                try {
                    DOData[0] = (byte) (DOData[0] ^ 0x08);
                    ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
                    if (Global.BioFaild(errorCode)) {
                        JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                    }
                    sleep((int) (Double.parseDouble(keeptime_job2.getText()) * 1000));//输出1 的时间
                    DOData[0] = (byte) (DOData[0] & 0xF7);
                    errorCode = instantDoCtrl.Write(0, DOData[0]);
                    if (Global.BioFaild(errorCode)) {
                        JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                    }
                    sleep((int) (Double.parseDouble(testtime_job2.getText()) * 1000));//输出0 的时间
                    int t = Integer.parseInt(testedCount_job2.getText());
                    if (t < Integer.parseInt(testcount_job2.getText())) {
                        t++;
                        testedCount_job2.setText(Integer.toString(t));//执行完一次 次数增1
                        write_job2();//将记录写入到硬盘
                    } else {
                        //自动执行停止按钮                       
                        configbut_job2.setEnabled(true);
                        stop_job2.setEnabled(false);
                        open_job2.setEnabled(true);
                        autorun_job2.setEnabled(true);
                        JOptionPane.showMessageDialog(null, "工件2测试完毕！请注意！");
                        if (dosub_job2 != null) {
                            dosub_job2.stop();
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(water_3job.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    class DOdataSubmit_job3_auto extends Thread {

        @Override
        public void run() {
            DOData[0] = (byte) (DOData[0] & 0xEF);
            while (true) {
                try {
                    DOData[0] = (byte) (DOData[0] ^ 0x10);
                    ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
                    if (Global.BioFaild(errorCode)) {
                        JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                    }
                    sleep((int) (Double.parseDouble(keeptime_job3.getText()) * 1000));//输出1 的时间
                    DOData[0] = (byte) (DOData[0] & 0xEF);
                    errorCode = instantDoCtrl.Write(0, DOData[0]);
                    if (Global.BioFaild(errorCode)) {
                        JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                    }
                    sleep((int) (Double.parseDouble(testtime_job3.getText()) * 1000));//输出0 的时间
                    int t = Integer.parseInt(testedCount_job3.getText());
                    if (t < Integer.parseInt(testcount_job3.getText())) {
                        t++;
                        testedCount_job3.setText(Integer.toString(t));//执行完一次 次数增1
                        write_job3();//将记录写入到硬盘
                    } else {
                        //自动执行停止按钮                       
                        configbut_job3.setEnabled(true);
                        stop_job3.setEnabled(false);
                        open_job3.setEnabled(true);
                        autorun_job3.setEnabled(true);
                        JOptionPane.showMessageDialog(null, "工件3测试完毕！请注意！");
                        if (dosub_job3 != null) {
                            dosub_job3.stop();
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(water_3job.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    class DOdataSubmit_job1_manual extends Thread {//执行一次

        @Override
        public void run() {
            DOData[0] = (byte) (DOData[0] & 0xFB);
            try {
                DOData[0] = (byte) (DOData[0] ^ 0x04);
                ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                }
                sleep((int) (Double.parseDouble(keeptime_job1.getText()) * 1000));//输出1 的时间
                DOData[0] = (byte) (DOData[0] & 0xFB);
                errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                }
                sleep((int) (Double.parseDouble(testtime_job1.getText()) * 1000));//输出0 的时间
            } catch (InterruptedException ex) {
                Logger.getLogger(water_3job.class.getName()).log(Level.SEVERE, null, ex);
            }
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            if (open_job1.getText() == "关闭") {
                open_job1.setText("打开");
            }
        }
    }

    class DOdataSubmit_job2_manual extends Thread {//执行一次

        @Override
        public void run() {
            DOData[0] = (byte) (DOData[0] & 0xF7);
            try {
                DOData[0] = (byte) (DOData[0] ^ 0x08);
                ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                }
                sleep((int) (Double.parseDouble(keeptime_job2.getText()) * 1000));//输出1 的时间
                DOData[0] = (byte) (DOData[0] & 0xF7);
                errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                }
                sleep((int) (Double.parseDouble(testtime_job2.getText()) * 1000));//输出0 的时间
            } catch (InterruptedException ex) {
                Logger.getLogger(water_3job.class.getName()).log(Level.SEVERE, null, ex);
            }
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            if (open_job2.getText() == "关闭") {
                open_job2.setText("打开");
            }
        }
    }

    class DOdataSubmit_job3_manual extends Thread {//执行一次

        @Override
        public void run() {
            DOData[0] = (byte) (DOData[0] & 0xEF);
            try {
                DOData[0] = (byte) (DOData[0] ^ 0x10);
                ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                }
                sleep((int) (Double.parseDouble(keeptime_job3.getText()) * 1000));//输出1 的时间
                DOData[0] = (byte) (DOData[0] & 0xEF);
                errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                }
                sleep((int) (Double.parseDouble(testtime_job3.getText()) * 1000));//输出0 的时间
            } catch (InterruptedException ex) {
                Logger.getLogger(water_3job.class.getName()).log(Level.SEVERE, null, ex);
            }
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            if (open_job3.getText() == "关闭") {
                open_job3.setText("打开");
            }
        }
    }
    double frequency = 0;

    class AIDataRecieve extends TimerTask {

        @Override
        public void run() {
            ErrorCode errorCode = instantAiCtrl.Read(configure.channelStart, configure.channelCount, AIData);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "读取模拟量数据失败！");
                return;
            }
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(2);
            frequency = AIData[5];
            frequency = (double) Math.round(frequency * 100) / 100;
            frequencydata[pose % 30] = frequency;
            pose++;
            bianpinqi.setText(String.valueOf(avg()));
            if (flag) {
                {
                    if (max_job1 < Math.abs(AIData[0] * 15 + adjust0)) {
                        max_job1 = AIData[0] * 15 + adjust0;
                    }
                    realtimePressure_job1.setText(nf.format(AIData[0] * 15 + adjust0));
                    waterpressure_job1.setText(nf.format(AIData[3] + adjust3));
                    if (!autorun_job1.isEnabled() & !(
                            (((AIData[0]) * 15 + adjust0) < Double.parseDouble(maxpush_job1_top.getText()))
                          &(((AIData[0]) * 15 + adjust0) > Double.parseDouble(maxpush_job1_down.getText()))
                            ) 
                            ) {
                             
                        warning_job1.setText("报警信息：\n当前最大操作力为:" + realtimePressure_job1.getText()
                                + "   已经超出设置的最大操作力范围");
                        //按下紧急停止按钮
                        if (dosub_job1 != null) {
                            dosub_job1.stop();
                        }
                        config_job1.setEnabled(true);
                        stop_job1.setEnabled(true);
                        open_job1.setEnabled(true);
                        autorun_job1.setEnabled(true);
                        configbut_job1.setEnabled(true);
                        DOData[0] = (byte) (DOData[0] & 0xFB);
                        errorCode = instantDoCtrl.Write(0, DOData[0]);
                        if (Global.BioFaild(errorCode)) {
                            JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                        }
                    } else if(!autorun_job1.isEnabled()){
                        warning_job1.setText("提示信息：\n当前最大操作力为:" + nf.format(max_job1) + " N");
                        warning_job1.setForeground(Color.BLUE);
                    }
                }
                
                {
                    if (max_job2 < Math.abs(AIData[1] * 15 + adjust1)) {
                        max_job2 = AIData[1] * 15 + adjust1;
                    }
                    realtimePressure_job2.setText(nf.format(AIData[1] * 15 + adjust1));
                    waterpressure_job2.setText(nf.format(AIData[3] + adjust3));
                    if (!autorun_job2.isEnabled() & !(
                            (((AIData[1]) * 15 + adjust1) < Double.parseDouble(maxpush_job2_top.getText()))
                          &(((AIData[1]) * 15 + adjust1) > Double.parseDouble(maxpush_job2_down.getText()))
                            ) 
                            ) {
                             
                        warning_job2.setText("报警信息：\n当前最大操作力为:" + realtimePressure_job2.getText()
                                + "   已经超出设置的最大操作力范围");
                        //按下紧急停止按钮
                        if (dosub_job2 != null) {
                            dosub_job2.stop();
                        }
                        config_job2.setEnabled(true);
                        stop_job2.setEnabled(true);
                        open_job2.setEnabled(true);
                        autorun_job2.setEnabled(true);
                        configbut_job2.setEnabled(true);
                        DOData[0] = (byte) (DOData[0] & 0xF7);
                        errorCode = instantDoCtrl.Write(0, DOData[0]);
                        if (Global.BioFaild(errorCode)) {
                            JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                        }
                    } else if(!autorun_job2.isEnabled()){
                        warning_job2.setText("提示信息：\n当前最大操作力为:" + nf.format(max_job2) + " N");
                        warning_job2.setForeground(Color.BLUE);
                    }
                }
                
                {
                    if (max_job3 < Math.abs(AIData[2] * 15 + adjust2)) {
                        max_job3 = AIData[2] * 15 + adjust2;
                    }
                    realtimePressure_job3.setText(nf.format(AIData[2] * 15 + adjust2));
                    waterpressure_job3.setText(nf.format(AIData[3] + adjust3));
                    if (!autorun_job3.isEnabled() & !(
                            (((AIData[2]) * 15 + adjust2) < Double.parseDouble(maxpush_job3_top.getText()))
                          &(((AIData[2]) * 15 + adjust2) > Double.parseDouble(maxpush_job3_down.getText()))
                            ) 
                            ) {
                             
                        warning_job3.setText("报警信息：\n当前最大操作力为:" + realtimePressure_job3.getText()
                                + "   已经超出设置的最大操作力范围");
                        //按下紧急停止按钮
                        if (dosub_job3 != null) {
                            dosub_job3.stop();
                        }
                        config_job3.setEnabled(true);
                        stop_job3.setEnabled(true);
                        open_job3.setEnabled(true);
                        autorun_job3.setEnabled(true);
                        configbut_job3.setEnabled(true);
                        DOData[0] = (byte) (DOData[0] & 0xEF);
                        errorCode = instantDoCtrl.Write(0, DOData[0]);
                        if (Global.BioFaild(errorCode)) {
                            JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                        }
                    } else if(!autorun_job3.isEnabled()){
                        warning_job3.setText("提示信息：\n当前最大操作力为:" + nf.format(max_job3) + " N");
                        warning_job3.setForeground(Color.BLUE);
                    }
                }
                
                
                temperature.setText("当前水温：" + nf.format(AIData[4] * 10 + adjust4) + "℃");//显示当前水温
            }
        }
    }

    public byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    class DIDataRecieve extends TimerTask {

        @Override
        public void run() {
            ErrorCode errorCode = instantDiCtrl.Read(0, 2, DIData);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "读取数字量数据失败！");
                return;
            }
            byte[] warn = getBooleanArray(DIData[0]);
            if (warn[0] == 0) 
                //if(false)
            {
                JOptionPane.showMessageDialog(null, "变频器报警！");
                //dosub_job1.stop();
                //关闭
                DOData[0] = (byte) (DOData[0] & 0xFE);//将开启位设置成0
                errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                } else {
                    AItimer.cancel();
                    DItimer.cancel();
                    if (dosub_job1 != null) {
                        dosub_job1.stop();
                    }
                    configbut_job1.setEnabled(true);
                    stop_job1.setEnabled(false);
                    open_job1.setEnabled(true);
                    autorun_job1.setEnabled(true);
                    if (dosub_job2 != null) {
                        dosub_job2.stop();
                    }
                    configbut_job2.setEnabled(true);
                    stop_job2.setEnabled(false);
                    open_job2.setEnabled(true);
                    autorun_job2.setEnabled(true);
                    if (dosub_job3 != null) {
                        dosub_job3.stop();
                    }
                    configbut_job3.setEnabled(true);
                    stop_job3.setEnabled(false);
                    open_job3.setEnabled(true);
                    autorun_job3.setEnabled(true);
                    pumpPressure.setEditable(true);
                    pump_Open.setEnabled(true);
                    pump_Close.setEnabled(false);
                    dynamic_Button.setEnabled(true);
                }
            }
        }
    }

    /**
     * Creates new form water_3job
     */
    public water_3job() {
        initComponents();
        DOData[0] = 0x00;
        DOData[1] = 0x00;
        configure.initial("PCI-1710HG,BID#0", 0, 6, "D:\\PCI1710.xml");//初始化数据采集卡
         //configure.initial("DemoDevice,BID#0", 0, 6, "D:\\DemoDevice.xml");//初始化数据采集卡
        IntialDevice();
        addWindowListener(new WindowCloseActionListener());
        read_job1();
        configbut_job1.setEnabled(true);
        read_job2();
        configbut_job2.setEnabled(true);
        read_job3();
        configbut_job3.setEnabled(true);
        this.setLocationRelativeTo(null);
        DOData[0] = 0x00;
        DOData[1] = 0x00;
        read_adjust();
    }

    public void IntialDevice() {
        try {
            instantDoCtrl.setSelectedDevice(new DeviceInformation(configure.deviceName));
            instantDiCtrl.setSelectedDevice(new DeviceInformation(configure.deviceName));
            instantAiCtrl.setSelectedDevice(new DeviceInformation(configure.deviceName));
            instantAoCtrl.setSelectedDevice(new DeviceInformation(configure.deviceName));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Sorry,some errors occured: " + ex.getMessage());
        }

        ErrorCode errorCode;
        errorCode = instantDoCtrl.LoadProfile(configure.profile);
        errorCode = instantDiCtrl.LoadProfile(configure.profile);
        errorCode = instantAiCtrl.LoadProfile(configure.profile);
        errorCode = instantAoCtrl.LoadProfile(configure.profile);
        if (Global.BioFaild(errorCode)) {
            JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
        }
        AItimer = new Timer();
        AItimer.schedule(new AIDataRecieve(), 0, 350);
    }

    class WindowCloseActionListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            if (instantAiCtrl != null) {
                instantAiCtrl.Cleanup();
            }
            write_job1();
            write_job2();
            write_job3();
            write_adjust();
            //将参数设置等数据写入硬盘
//            if (!initialButton_job1.isEnabled()) {
//             
//            }
//            if (!initialButton_job2.isEnabled()) {
//                
//            }
//            if (!initialButton_job3.isEnabled()) {
//              
//            }

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        config_job1 = new javax.swing.JDialog();
        test_time = new javax.swing.JLabel();
        testtime_job1 = new javax.swing.JTextField();
        max_push = new javax.swing.JLabel();
        maxpush_job1_top = new javax.swing.JTextField();
        test_count = new javax.swing.JLabel();
        testcount_job1 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        keeptime_job1 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        maxpush_job1_down = new javax.swing.JTextField();
        max_push3 = new javax.swing.JLabel();
        max_push4 = new javax.swing.JLabel();
        config_job2 = new javax.swing.JDialog();
        test_time1 = new javax.swing.JLabel();
        testtime_job2 = new javax.swing.JTextField();
        max_push1 = new javax.swing.JLabel();
        maxpush_job2_top = new javax.swing.JTextField();
        test_count1 = new javax.swing.JLabel();
        testcount_job2 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        keeptime_job2 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        maxpush_job2_down = new javax.swing.JTextField();
        max_push5 = new javax.swing.JLabel();
        max_push6 = new javax.swing.JLabel();
        config_job3 = new javax.swing.JDialog();
        test_time2 = new javax.swing.JLabel();
        testtime_job3 = new javax.swing.JTextField();
        max_push2 = new javax.swing.JLabel();
        maxpush_job3_top = new javax.swing.JTextField();
        test_count2 = new javax.swing.JLabel();
        testcount_job3 = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        keeptime_job3 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        maxpush_job3_down = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        max_push7 = new javax.swing.JLabel();
        max_push8 = new javax.swing.JLabel();
        welcome = new javax.swing.JDialog();
        jLabel28 = new javax.swing.JLabel();
        entrance = new javax.swing.JButton();
        exit = new javax.swing.JButton();
        SetPumpPresure = new javax.swing.JDialog();
        jLabel32 = new javax.swing.JLabel();
        modifypresure = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        modifyOK = new javax.swing.JButton();
        modifyCancel = new javax.swing.JButton();
        adjust_Dialog = new javax.swing.JDialog();
        jLabel7 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        push_adjust1 = new javax.swing.JTextField();
        push_adjust2 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        push_adjust3 = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        waterpressure_adjust = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        water_adjust = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        job1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        autorun_job1 = new javax.swing.JButton();
        open_job1 = new javax.swing.JButton();
        stop_job1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        realtimePressure_job1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        testedCount_job1 = new javax.swing.JTextField();
        configbut_job1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        waterpressure_job1 = new javax.swing.JTextField();
        warning_job1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        reset_job1 = new javax.swing.JButton();
        watersup_job1 = new javax.swing.JButton();
        switch_job1 = new javax.swing.JButton();
        jLabel43 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        job2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        testedCount_job2 = new javax.swing.JTextField();
        configbut_job2 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        autorun_job2 = new javax.swing.JButton();
        waterpressure_job2 = new javax.swing.JTextField();
        open_job2 = new javax.swing.JButton();
        stop_job2 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        realtimePressure_job2 = new javax.swing.JTextField();
        warning_job2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        reset_job2 = new javax.swing.JButton();
        watersup_job2 = new javax.swing.JButton();
        switch_job2 = new javax.swing.JButton();
        job3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        testedCount_job3 = new javax.swing.JTextField();
        configbut_job3 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        autorun_job3 = new javax.swing.JButton();
        waterpressure_job3 = new javax.swing.JTextField();
        open_job3 = new javax.swing.JButton();
        stop_job3 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        realtimePressure_job3 = new javax.swing.JTextField();
        warning_job3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        reset_job3 = new javax.swing.JButton();
        watersup_job3 = new javax.swing.JButton();
        switch_job3 = new javax.swing.JButton();
        system = new javax.swing.JPanel();
        pump_Open = new javax.swing.JButton();
        pump_Close = new javax.swing.JButton();
        pumpPressure = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        dynamic_Button = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        temperature = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        adjust = new javax.swing.JButton();
        static_Button = new javax.swing.JButton();
        bianpinqi = new javax.swing.JLabel();

        config_job1.setMinimumSize(new java.awt.Dimension(300, 500));
        config_job1.setResizable(false);

        test_time.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        test_time.setText("测试时间：");

        testtime_job1.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        testtime_job1.setText("0");

        max_push.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        max_push.setText("下限：");

        maxpush_job1_top.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        maxpush_job1_top.setText("0");
        maxpush_job1_top.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxpush_job1_topActionPerformed(evt);
            }
        });

        test_count.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        test_count.setText("测试次数：");

        testcount_job1.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        testcount_job1.setText("0");
        testcount_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testcount_job1ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jButton5.setText("确定");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jButton6.setText("取消");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("黑体", 0, 30)); // NOI18N
        jLabel6.setText("工件1参数设置");

        jLabel25.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel25.setText("保持时间：");

        keeptime_job1.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        keeptime_job1.setText("0");
        keeptime_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keeptime_job1ActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel24.setText("S");

        jLabel31.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel31.setText("N");

        jLabel33.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel33.setText("S");

        jLabel41.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel41.setText("N");

        maxpush_job1_down.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        maxpush_job1_down.setText("0");
        maxpush_job1_down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxpush_job1_downActionPerformed(evt);
            }
        });

        max_push3.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        max_push3.setText("最大操作力：");

        max_push4.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        max_push4.setText("上限：");

        javax.swing.GroupLayout config_job1Layout = new javax.swing.GroupLayout(config_job1.getContentPane());
        config_job1.getContentPane().setLayout(config_job1Layout);
        config_job1Layout.setHorizontalGroup(
            config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(config_job1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(config_job1Layout.createSequentialGroup()
                        .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(config_job1Layout.createSequentialGroup()
                                .addComponent(jButton5)
                                .addGap(26, 26, 26)
                                .addComponent(jButton6))
                            .addGroup(config_job1Layout.createSequentialGroup()
                                .addComponent(test_time)
                                .addGap(34, 34, 34)
                                .addComponent(testtime_job1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(config_job1Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(keeptime_job1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6)
                            .addGroup(config_job1Layout.createSequentialGroup()
                                .addComponent(test_count)
                                .addGap(34, 34, 34)
                                .addComponent(testcount_job1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(jLabel33)))
                    .addComponent(max_push3)
                    .addGroup(config_job1Layout.createSequentialGroup()
                        .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(max_push)
                            .addComponent(max_push4))
                        .addGap(74, 74, 74)
                        .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(config_job1Layout.createSequentialGroup()
                                .addComponent(maxpush_job1_down, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel41))
                            .addGroup(config_job1Layout.createSequentialGroup()
                                .addComponent(maxpush_job1_top, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel31)))))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        config_job1Layout.setVerticalGroup(
            config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(config_job1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(38, 38, 38)
                .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(test_time)
                    .addComponent(testtime_job1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(max_push3)
                .addGap(2, 2, 2)
                .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxpush_job1_top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(max_push4))
                .addGap(4, 4, 4)
                .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxpush_job1_down, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41)
                    .addComponent(max_push))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(test_count)
                    .addComponent(testcount_job1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel25)
                    .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(keeptime_job1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel33)))
                .addGap(18, 18, 18)
                .addGroup(config_job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        config_job1.setLocationRelativeTo(null);

        config_job2.setMinimumSize(new java.awt.Dimension(300, 500));
        config_job2.setResizable(false);

        test_time1.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        test_time1.setText("测试时间：");

        testtime_job2.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        testtime_job2.setText("0");

        max_push1.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        max_push1.setText("上限：");

        maxpush_job2_top.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        maxpush_job2_top.setText("0");

        test_count1.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        test_count1.setText("测试次数：");

        testcount_job2.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        testcount_job2.setText("0");
        testcount_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testcount_job2ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jButton7.setText("确定");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jButton8.setText("取消");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("黑体", 0, 30)); // NOI18N
        jLabel18.setText("工件2参数设置");

        jLabel26.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel26.setText("保持时间：");

        keeptime_job2.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        keeptime_job2.setText("0");
        keeptime_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keeptime_job2ActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel34.setText("N");

        jLabel35.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel35.setText("S");

        jLabel36.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel36.setText("S");

        jLabel46.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel46.setText("N");

        maxpush_job2_down.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        maxpush_job2_down.setText("0");

        max_push5.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        max_push5.setText("最大操作力：");

        max_push6.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        max_push6.setText("下限：");

        javax.swing.GroupLayout config_job2Layout = new javax.swing.GroupLayout(config_job2.getContentPane());
        config_job2.getContentPane().setLayout(config_job2Layout);
        config_job2Layout.setHorizontalGroup(
            config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(config_job2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(config_job2Layout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addGap(26, 26, 26)
                        .addComponent(jButton8))
                    .addComponent(jLabel18)
                    .addGroup(config_job2Layout.createSequentialGroup()
                        .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(config_job2Layout.createSequentialGroup()
                                    .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(test_count1)
                                        .addComponent(test_time1))
                                    .addGap(26, 26, 26))
                                .addGroup(config_job2Layout.createSequentialGroup()
                                    .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(max_push6)
                                        .addComponent(max_push1))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                            .addGroup(config_job2Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addGap(26, 26, 26)))
                        .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(config_job2Layout.createSequentialGroup()
                                .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(testtime_job2, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                                    .addComponent(testcount_job2)
                                    .addGroup(config_job2Layout.createSequentialGroup()
                                        .addComponent(maxpush_job2_top, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel34))
                                    .addGroup(config_job2Layout.createSequentialGroup()
                                        .addComponent(maxpush_job2_down, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel46)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36))
                            .addGroup(config_job2Layout.createSequentialGroup()
                                .addComponent(keeptime_job2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel35))))
                    .addComponent(max_push5))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        config_job2Layout.setVerticalGroup(
            config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(config_job2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addGap(38, 38, 38)
                .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(test_time1)
                        .addComponent(testtime_job2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(max_push5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxpush_job2_top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34)
                    .addComponent(max_push1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(max_push6)
                    .addComponent(maxpush_job2_down, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(test_count1)
                    .addComponent(testcount_job2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(keeptime_job2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addGap(18, 18, 18)
                .addGroup(config_job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton8))
                .addGap(29, 29, 29))
        );

        config_job2.setLocationRelativeTo(null);

        config_job3.setMinimumSize(new java.awt.Dimension(300, 500));
        config_job3.setResizable(false);

        test_time2.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        test_time2.setText("测试时间：");

        testtime_job3.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        testtime_job3.setText("0");

        max_push2.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        max_push2.setText("上限：");

        maxpush_job3_top.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        maxpush_job3_top.setText("0");

        test_count2.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        test_count2.setText("测试次数：");

        testcount_job3.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        testcount_job3.setText("0");
        testcount_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testcount_job3ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jButton9.setText("确定");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jButton10.setText("取消");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("黑体", 0, 30)); // NOI18N
        jLabel20.setText("工件3参数设置");

        jLabel27.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel27.setText("保持时间：");

        keeptime_job3.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        keeptime_job3.setText("0");
        keeptime_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keeptime_job3ActionPerformed(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel37.setText("N");

        jLabel38.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel38.setText("S");

        jLabel39.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel39.setText("S");

        maxpush_job3_down.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        maxpush_job3_down.setText("0");

        jLabel52.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        jLabel52.setText("N");

        max_push7.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        max_push7.setText("最大操作力：");

        max_push8.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        max_push8.setText("下限：");

        javax.swing.GroupLayout config_job3Layout = new javax.swing.GroupLayout(config_job3.getContentPane());
        config_job3.getContentPane().setLayout(config_job3Layout);
        config_job3Layout.setHorizontalGroup(
            config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(config_job3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(config_job3Layout.createSequentialGroup()
                        .addComponent(jButton9)
                        .addGap(26, 26, 26)
                        .addComponent(jButton10)
                        .addGap(62, 62, 62))
                    .addGroup(config_job3Layout.createSequentialGroup()
                        .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(max_push7))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(config_job3Layout.createSequentialGroup()
                        .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, config_job3Layout.createSequentialGroup()
                                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27)
                                    .addComponent(test_count2))
                                .addGap(43, 43, 43)
                                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(testcount_job3)
                                    .addComponent(keeptime_job3)))
                            .addGroup(config_job3Layout.createSequentialGroup()
                                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(config_job3Layout.createSequentialGroup()
                                        .addComponent(test_time2)
                                        .addGap(0, 26, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, config_job3Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(max_push2, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(max_push8, javax.swing.GroupLayout.Alignment.TRAILING))))
                                .addGap(18, 18, 18)
                                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(testtime_job3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                    .addComponent(maxpush_job3_top, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(maxpush_job3_down, javax.swing.GroupLayout.Alignment.LEADING))))
                        .addGap(18, 18, 18)
                        .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39)
                            .addComponent(jLabel37)
                            .addComponent(jLabel52)
                            .addComponent(jLabel38))
                        .addContainerGap())))
        );
        config_job3Layout.setVerticalGroup(
            config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(config_job3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addGap(41, 41, 41)
                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(test_time2)
                        .addComponent(testtime_job3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel39))
                .addGap(18, 18, 18)
                .addComponent(max_push7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxpush_job3_top, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(max_push2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(max_push8)
                    .addComponent(maxpush_job3_down, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(test_count2)
                    .addComponent(testcount_job3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(keeptime_job3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(config_job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton10))
                .addGap(26, 26, 26))
        );

        config_job3.setLocationRelativeTo(null);

        welcome.setMinimumSize(new java.awt.Dimension(402, 308));

        jLabel28.setFont(new java.awt.Font("华文彩云", 0, 36)); // NOI18N
        jLabel28.setText("欢迎使用本系统！！！");

        entrance.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        entrance.setText("进入系统");
        entrance.setMaximumSize(new java.awt.Dimension(400, 300));
        entrance.setMinimumSize(new java.awt.Dimension(400, 300));
        entrance.setPreferredSize(new java.awt.Dimension(400, 300));
        entrance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entranceActionPerformed(evt);
            }
        });

        exit.setFont(new java.awt.Font("黑体", 0, 20)); // NOI18N
        exit.setText("退出系统");
        exit.setMinimumSize(new java.awt.Dimension(400, 300));
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout welcomeLayout = new javax.swing.GroupLayout(welcome.getContentPane());
        welcome.getContentPane().setLayout(welcomeLayout);
        welcomeLayout.setHorizontalGroup(
            welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(welcomeLayout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 14, Short.MAX_VALUE))
                    .addGroup(welcomeLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(entrance, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(exit, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        welcomeLayout.setVerticalGroup(
            welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomeLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(welcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(entrance, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(exit, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(55, Short.MAX_VALUE))
        );

        SetPumpPresure.setMinimumSize(new java.awt.Dimension(432, 200));

        jLabel32.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel32.setText("请确认您要修改的压力参数：");

        modifypresure.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N

        jLabel40.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel40.setText("bar");

        modifyOK.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        modifyOK.setText("确定");
        modifyOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyOKActionPerformed(evt);
            }
        });

        modifyCancel.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        modifyCancel.setText("取消");
        modifyCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SetPumpPresureLayout = new javax.swing.GroupLayout(SetPumpPresure.getContentPane());
        SetPumpPresure.getContentPane().setLayout(SetPumpPresureLayout);
        SetPumpPresureLayout.setHorizontalGroup(
            SetPumpPresureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SetPumpPresureLayout.createSequentialGroup()
                .addGroup(SetPumpPresureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(SetPumpPresureLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modifypresure, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(SetPumpPresureLayout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(modifyOK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(modifyCancel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40)
                .addContainerGap(53, Short.MAX_VALUE))
        );
        SetPumpPresureLayout.setVerticalGroup(
            SetPumpPresureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SetPumpPresureLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(SetPumpPresureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(modifypresure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addGap(18, 18, 18)
                .addGroup(SetPumpPresureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modifyOK)
                    .addComponent(modifyCancel))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        SetPumpPresure.setLocationRelativeTo(null);

        adjust_Dialog.setMinimumSize(new java.awt.Dimension(265, 650));

        jLabel7.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel7.setText("工件1误差调校：");

        jLabel44.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel44.setText("操作力：");

        push_adjust1.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        push_adjust1.setText("0");
        push_adjust1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                push_adjust1ActionPerformed(evt);
            }
        });

        push_adjust2.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        push_adjust2.setText("0");
        push_adjust2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                push_adjust2ActionPerformed(evt);
            }
        });

        jLabel45.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel45.setText("工件2误差调校：");

        jLabel47.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel47.setText("操作力：");

        push_adjust3.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        push_adjust3.setText("0");
        push_adjust3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                push_adjust3ActionPerformed(evt);
            }
        });

        jLabel48.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel48.setText("工件3误差调校：");

        jLabel49.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel49.setText("水流压力：");

        waterpressure_adjust.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        waterpressure_adjust.setText("0");
        waterpressure_adjust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterpressure_adjustActionPerformed(evt);
            }
        });

        jLabel50.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel50.setText("操作力：");

        jLabel51.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jLabel51.setText("水温：");

        water_adjust.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        water_adjust.setText("0");
        water_adjust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                water_adjustActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jButton2.setText("确定");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
        jButton3.setText("取消");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout adjust_DialogLayout = new javax.swing.GroupLayout(adjust_Dialog.getContentPane());
        adjust_Dialog.getContentPane().setLayout(adjust_DialogLayout);
        adjust_DialogLayout.setHorizontalGroup(
            adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adjust_DialogLayout.createSequentialGroup()
                .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel48)
                        .addGroup(adjust_DialogLayout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(adjust_DialogLayout.createSequentialGroup()
                                    .addComponent(jLabel51)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(water_adjust, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(adjust_DialogLayout.createSequentialGroup()
                                    .addComponent(jButton2)
                                    .addGap(62, 62, 62)
                                    .addComponent(jButton3))
                                .addGroup(adjust_DialogLayout.createSequentialGroup()
                                    .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel50)
                                        .addComponent(jLabel49))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(waterpressure_adjust, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(push_adjust3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addComponent(jLabel45)
                        .addComponent(jLabel7))
                    .addGroup(adjust_DialogLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(adjust_DialogLayout.createSequentialGroup()
                                .addComponent(jLabel47)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(push_adjust2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(adjust_DialogLayout.createSequentialGroup()
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(push_adjust1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        adjust_DialogLayout.setVerticalGroup(
            adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adjust_DialogLayout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(push_adjust1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(76, 76, 76)
                .addComponent(jLabel45)
                .addGap(18, 18, 18)
                .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(push_adjust2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addComponent(jLabel48)
                .addGap(18, 18, 18)
                .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(push_adjust3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(waterpressure_adjust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(water_adjust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(adjust_DialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        adjust_Dialog.setLocationRelativeTo(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1078, 716));

        job1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel1.setText("1工位");

        autorun_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        autorun_job1.setText("自动运行");
        autorun_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autorun_job1ActionPerformed(evt);
            }
        });

        open_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        open_job1.setText("打开");
        open_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open_job1ActionPerformed(evt);
            }
        });

        stop_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        stop_job1.setText("紧急停止");
        stop_job1.setEnabled(false);
        stop_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop_job1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel2.setText("瞬时操作力：");

        realtimePressure_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        realtimePressure_job1.setText("0");
        realtimePressure_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                realtimePressure_job1ActionPerformed(evt);
            }
        });
        realtimePressure_job1.setEditable(false);

        jLabel4.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel4.setText("已测次数：");

        testedCount_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        testedCount_job1.setText("0");
        testedCount_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testedCount_job1ActionPerformed(evt);
            }
        });

        configbut_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        configbut_job1.setText("参数设置");
        configbut_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configbut_job1ActionPerformed(evt);
            }
        });
        configbut_job1.setEnabled(false);

        jLabel5.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel5.setText("水流压力：");

        waterpressure_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        waterpressure_job1.setText("0");
        waterpressure_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterpressure_job1ActionPerformed(evt);
            }
        });
        waterpressure_job1.setEditable(false);

        warning_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        warning_job1.setForeground(new java.awt.Color(255, 0, 0));
        warning_job1.setText("报警信息：");

        jLabel3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel3.setText("N");

        jLabel21.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel21.setText("bar");

        reset_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        reset_job1.setText("复位");
        reset_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset_job1ActionPerformed(evt);
            }
        });

        watersup_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        watersup_job1.setText("供水开启");
        watersup_job1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                watersup_job1ItemStateChanged(evt);
            }
        });
        watersup_job1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                watersup_job1StateChanged(evt);
            }
        });
        watersup_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                watersup_job1ActionPerformed(evt);
            }
        });

        switch_job1.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        switch_job1.setText("向下");
        switch_job1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switch_job1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout job1Layout = new javax.swing.GroupLayout(job1);
        job1.setLayout(job1Layout);
        job1Layout.setHorizontalGroup(
            job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(job1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(job1Layout.createSequentialGroup()
                        .addGroup(job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(warning_job1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(job1Layout.createSequentialGroup()
                                .addGroup(job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, job1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(testedCount_job1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, job1Layout.createSequentialGroup()
                                        .addComponent(open_job1)
                                        .addGap(54, 54, 54)
                                        .addComponent(switch_job1)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                                .addGroup(job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, job1Layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(waterpressure_job1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel21))
                                    .addComponent(autorun_job1, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(36, 36, 36)
                                .addGroup(job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(stop_job1)
                                    .addGroup(job1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(realtimePressure_job1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(reset_job1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(watersup_job1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(configbut_job1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        job1Layout.setVerticalGroup(
            job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(job1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(job1Layout.createSequentialGroup()
                        .addComponent(reset_job1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(watersup_job1)
                            .addComponent(configbut_job1)))
                    .addGroup(job1Layout.createSequentialGroup()
                        .addGroup(job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(autorun_job1)
                            .addComponent(stop_job1)
                            .addComponent(open_job1)
                            .addComponent(switch_job1))
                        .addGap(6, 6, 6)
                        .addGroup(job1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(testedCount_job1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(waterpressure_job1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21)
                            .addComponent(jLabel2)
                            .addComponent(realtimePressure_job1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(warning_job1)))
                .addGap(12, 12, 12))
        );

        jLabel43.setFont(new java.awt.Font("华文行楷", 1, 36)); // NOI18N
        jLabel43.setText("冲洗阀寿命测试机");

        jLabel42.setFont(new java.awt.Font("华文行楷", 1, 36)); // NOI18N
        jLabel42.setText("苏州华测工程检测有限公司");

        job2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel8.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel8.setText("已测次数：");

        testedCount_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        testedCount_job2.setText("0");

        configbut_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        configbut_job2.setText("参数设置");
        configbut_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configbut_job2ActionPerformed(evt);
            }
        });
        configbut_job2.setEnabled(false);

        jLabel9.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel9.setText("2工位");

        jLabel10.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel10.setText("水流压力：");

        autorun_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        autorun_job2.setText("自动运行");
        autorun_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autorun_job2ActionPerformed(evt);
            }
        });

        waterpressure_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        waterpressure_job2.setText("0");
        waterpressure_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterpressure_job2ActionPerformed(evt);
            }
        });
        waterpressure_job2.setEditable(false);

        open_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        open_job2.setText("打开");
        open_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open_job2ActionPerformed(evt);
            }
        });

        stop_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        stop_job2.setText("紧急停止");
        stop_job2.setEnabled(false);
        stop_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop_job2ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel11.setText("瞬时操作力：");

        realtimePressure_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        realtimePressure_job2.setText("0");
        realtimePressure_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                realtimePressure_job2ActionPerformed(evt);
            }
        });
        realtimePressure_job2.setEditable(false);

        warning_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        warning_job2.setForeground(new java.awt.Color(255, 0, 0));
        warning_job2.setText("报警信息：");

        jLabel12.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel12.setText("N");

        jLabel23.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel23.setText("bar");

        reset_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        reset_job2.setText("复位");
        reset_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset_job2ActionPerformed(evt);
            }
        });

        watersup_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        watersup_job2.setText("供水开启");
        watersup_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                watersup_job2ActionPerformed(evt);
            }
        });

        switch_job2.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        switch_job2.setText("向下");
        switch_job2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switch_job2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout job2Layout = new javax.swing.GroupLayout(job2);
        job2.setLayout(job2Layout);
        job2Layout.setHorizontalGroup(
            job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(job2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(job2Layout.createSequentialGroup()
                        .addGroup(job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(job2Layout.createSequentialGroup()
                                .addComponent(open_job2)
                                .addGap(57, 57, 57)
                                .addComponent(switch_job2))
                            .addGroup(job2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(testedCount_job2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(43, 43, 43)
                        .addGroup(job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(job2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(waterpressure_job2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23))
                            .addComponent(autorun_job2))
                        .addGap(41, 41, 41)
                        .addGroup(job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(job2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(realtimePressure_job2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12))
                            .addComponent(stop_job2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(watersup_job2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(reset_job2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(configbut_job2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9)
                    .addComponent(warning_job2, javax.swing.GroupLayout.PREFERRED_SIZE, 733, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        job2Layout.setVerticalGroup(
            job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(job2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(job2Layout.createSequentialGroup()
                        .addGroup(job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(open_job2)
                            .addComponent(switch_job2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(testedCount_job2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(job2Layout.createSequentialGroup()
                        .addComponent(stop_job2)
                        .addGap(28, 28, 28))
                    .addGroup(job2Layout.createSequentialGroup()
                        .addComponent(autorun_job2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(waterpressure_job2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23)
                            .addComponent(jLabel11)
                            .addComponent(realtimePressure_job2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)))
                    .addGroup(job2Layout.createSequentialGroup()
                        .addComponent(reset_job2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(job2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(watersup_job2)
                            .addComponent(configbut_job2))))
                .addGap(2, 2, 2)
                .addComponent(warning_job2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        job3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel13.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel13.setText("已测次数：");

        testedCount_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        testedCount_job3.setText("0");
        testedCount_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testedCount_job3ActionPerformed(evt);
            }
        });

        configbut_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        configbut_job3.setText("参数设置");
        configbut_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configbut_job3ActionPerformed(evt);
            }
        });
        configbut_job3.setEnabled(false);

        jLabel14.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel14.setText("3工位");

        jLabel15.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel15.setText("水流压力：");

        autorun_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        autorun_job3.setText("自动运行");
        autorun_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autorun_job3ActionPerformed(evt);
            }
        });

        waterpressure_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        waterpressure_job3.setText("0");
        waterpressure_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterpressure_job3ActionPerformed(evt);
            }
        });
        waterpressure_job3.setEditable(false);

        open_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        open_job3.setText("打开");
        open_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open_job3ActionPerformed(evt);
            }
        });

        stop_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        stop_job3.setText("紧急停止");
        stop_job3.setEnabled(false);
        stop_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop_job3ActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel16.setText("瞬时操作力：");

        realtimePressure_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        realtimePressure_job3.setText("0");
        realtimePressure_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                realtimePressure_job3ActionPerformed(evt);
            }
        });
        realtimePressure_job3.setEditable(false);

        warning_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        warning_job3.setForeground(new java.awt.Color(255, 0, 0));
        warning_job3.setText("报警信息：");

        jLabel17.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel17.setText("N");

        jLabel22.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        jLabel22.setText("bar");

        reset_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        reset_job3.setText("复位");
        reset_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset_job3ActionPerformed(evt);
            }
        });

        watersup_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        watersup_job3.setText("供水开启");
        watersup_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                watersup_job3ActionPerformed(evt);
            }
        });

        switch_job3.setFont(new java.awt.Font("黑体", 0, 18)); // NOI18N
        switch_job3.setText("向下");
        switch_job3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switch_job3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout job3Layout = new javax.swing.GroupLayout(job3);
        job3.setLayout(job3Layout);
        job3Layout.setHorizontalGroup(
            job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(job3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(warning_job3, javax.swing.GroupLayout.PREFERRED_SIZE, 774, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(job3Layout.createSequentialGroup()
                        .addGroup(job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addGroup(job3Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(testedCount_job3, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(job3Layout.createSequentialGroup()
                                .addComponent(open_job3)
                                .addGap(60, 60, 60)
                                .addComponent(switch_job3)))
                        .addGroup(job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(job3Layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(waterpressure_job3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22))
                            .addGroup(job3Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(autorun_job3)))
                        .addGap(38, 38, 38)
                        .addGroup(job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(job3Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(realtimePressure_job3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17))
                            .addComponent(stop_job3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(reset_job3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(watersup_job3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(configbut_job3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        job3Layout.setVerticalGroup(
            job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(job3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, job3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, job3Layout.createSequentialGroup()
                                .addGroup(job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(autorun_job3)
                                    .addComponent(open_job3)
                                    .addComponent(switch_job3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(testedCount_job3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel15)
                                    .addComponent(waterpressure_job3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel16)
                                    .addComponent(realtimePressure_job3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, job3Layout.createSequentialGroup()
                                .addComponent(stop_job3)
                                .addGap(36, 36, 36))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, job3Layout.createSequentialGroup()
                        .addComponent(reset_job3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(job3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(watersup_job3)
                            .addComponent(configbut_job3))))
                .addComponent(warning_job3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        system.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        pump_Open.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        pump_Open.setText("开启");
        pump_Open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pump_OpenActionPerformed(evt);
            }
        });

        pump_Close.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        pump_Close.setText("关闭");
        pump_Close.setEnabled(false);
        pump_Close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pump_CloseActionPerformed(evt);
            }
        });

        pumpPressure.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        pumpPressure.setText("10");
        pumpPressure.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                pumpPressureFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt) {
                pumpPressureFocusGained(evt);
            }
        });
        pumpPressure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pumpPressureActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        jLabel29.setText("定变频选择：");

        dynamic_Button.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        dynamic_Button.setText("变频");
        dynamic_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dynamic_ButtonActionPerformed(evt);
            }
        });
        dynamic_Button.setEnabled(false);

        jLabel30.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        jLabel30.setText("水泵开关：");

        temperature.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        temperature.setText("当前水温：");

        jLabel19.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        jLabel19.setText("bar");

        jButton1.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        jButton1.setText("水泵压力：");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        adjust.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        adjust.setText("系统误差修正");
        adjust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adjustActionPerformed(evt);
            }
        });

        static_Button.setFont(new java.awt.Font("隶书", 1, 18)); // NOI18N
        static_Button.setText("定频");
        static_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                static_ButtonActionPerformed(evt);
            }
        });
        dynamic_Button.setToolTipText("此时为定频");

        javax.swing.GroupLayout systemLayout = new javax.swing.GroupLayout(system);
        system.setLayout(systemLayout);
        systemLayout.setHorizontalGroup(
            systemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(systemLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(systemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(systemLayout.createSequentialGroup()
                        .addComponent(temperature)
                        .addGap(76, 76, 76))
                    .addGroup(systemLayout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dynamic_Button)
                        .addGap(18, 18, 18)
                        .addComponent(static_Button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(systemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(systemLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pumpPressure, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addGap(60, 60, 60)
                        .addComponent(jLabel30)
                        .addGap(43, 43, 43)
                        .addComponent(pump_Open)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pump_Close))
                    .addComponent(adjust))
                .addContainerGap(163, Short.MAX_VALUE))
        );
        systemLayout.setVerticalGroup(
            systemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(systemLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(systemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(systemLayout.createSequentialGroup()
                        .addGroup(systemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(systemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel30)
                                .addComponent(pump_Open)
                                .addComponent(pump_Close))
                            .addGroup(systemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel29)
                                .addComponent(dynamic_Button)))
                        .addGap(18, 18, 18)
                        .addComponent(temperature)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(systemLayout.createSequentialGroup()
                        .addGroup(systemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pumpPressure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(jButton1)
                            .addComponent(static_Button))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(adjust)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        bianpinqi.setText("jLabel41");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(job3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(job1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(job2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(system, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 419, Short.MAX_VALUE)
                        .addComponent(jLabel43)
                        .addGap(386, 386, 386))))
            .addGroup(layout.createSequentialGroup()
                .addGap(354, 354, 354)
                .addComponent(bianpinqi)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel42)
                .addGap(307, 307, 307))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(job1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(job2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(job3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(system, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(bianpinqi))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void autorun_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autorun_job1ActionPerformed
        // TODO add your handling code here:
        if (pump_Open.isEnabled()) {
            JOptionPane.showMessageDialog(null, "请先打开水泵！");
        } else if (watersup_job1.getText() == "供水开启") {
            JOptionPane.showMessageDialog(null, "请先打开供水开关！");
        } else if (testcount_job1.getText() == "0") {
            JOptionPane.showMessageDialog(null, "参数设置非法，请重新设置！");
        } else {
            dosub_job1 = new DOdataSubmit_job1_auto();
            dosub_job1.start();
            configbut_job1.setEnabled(false);
            stop_job1.setEnabled(true);
            open_job1.setEnabled(false);
            autorun_job1.setEnabled(false);
        }
    }//GEN-LAST:event_autorun_job1ActionPerformed

    private void realtimePressure_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realtimePressure_job1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_realtimePressure_job1ActionPerformed

    private void configbut_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configbut_job1ActionPerformed
        // TODO add your handling code here:
        lastTesttime = testtime_job1.getText();
        lastMaxpush = maxpush_job1_top.getText();
        lastTestcount = testcount_job1.getText();
        // lastPumppressure = waterPressure_job1.getText();
        lastKeeptime = keeptime_job1.getText();
        config_job1.show();
    }//GEN-LAST:event_configbut_job1ActionPerformed

    private void waterpressure_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterpressure_job1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterpressure_job1ActionPerformed

    private void testcount_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testcount_job1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_testcount_job1ActionPerformed

    private void configbut_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configbut_job2ActionPerformed
        // TODO add your handling code here:
        lastTesttime = testtime_job2.getText();
        lastMaxpush = maxpush_job2_top.getText();
        lastTestcount = testcount_job2.getText();
        // lastPumppressure = waterPressure_job2.getText();
        lastKeeptime = keeptime_job2.getText();
        config_job2.show();
    }//GEN-LAST:event_configbut_job2ActionPerformed

    private void autorun_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autorun_job2ActionPerformed
        // TODO add your handling code here:
        if (pump_Open.isEnabled()) {
            JOptionPane.showMessageDialog(null, "请先打开水泵！");
        } else if (watersup_job2.getText() == "供水开启") {
            JOptionPane.showMessageDialog(null, "请先打开供水开关！");
        } else if (testcount_job2.getText() == "0") {
            JOptionPane.showMessageDialog(null, "参数设置非法，请重新设置！");
        } else {
            dosub_job2 = new DOdataSubmit_job2_auto();
            dosub_job2.start();
            configbut_job2.setEnabled(false);
            stop_job2.setEnabled(true);
            open_job2.setEnabled(false);
            autorun_job2.setEnabled(false);
        }
    }//GEN-LAST:event_autorun_job2ActionPerformed

    private void waterpressure_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterpressure_job2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterpressure_job2ActionPerformed

    private void realtimePressure_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realtimePressure_job2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_realtimePressure_job2ActionPerformed

    private void configbut_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configbut_job3ActionPerformed
        lastTesttime = testtime_job3.getText();
        lastMaxpush = maxpush_job3_top.getText();
        lastTestcount = testcount_job3.getText();
        // lastPumppressure = waterPressure_job3.getText();
        lastKeeptime = keeptime_job3.getText();
        // TODO add your handling code here:
        config_job3.show();
    }//GEN-LAST:event_configbut_job3ActionPerformed

    private void autorun_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autorun_job3ActionPerformed
        // TODO add your handling code here:
        if (pump_Open.isEnabled()) {
            JOptionPane.showMessageDialog(null, "请先打开水泵！");
        } else if (watersup_job3.getText() == "供水开启") {
            JOptionPane.showMessageDialog(null, "请先打开供水开关！");
        } else if (testcount_job3.getText() == "0") {
            JOptionPane.showMessageDialog(null, "参数设置非法，请重新设置！");
        } else {
            dosub_job3 = new DOdataSubmit_job3_auto();
            dosub_job3.start();
            configbut_job3.setEnabled(false);
            stop_job3.setEnabled(true);
            open_job3.setEnabled(false);
            autorun_job3.setEnabled(false);
        }
    }//GEN-LAST:event_autorun_job3ActionPerformed

    private void waterpressure_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterpressure_job3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterpressure_job3ActionPerformed

    private void realtimePressure_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realtimePressure_job3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_realtimePressure_job3ActionPerformed

    private void testedCount_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testedCount_job3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_testedCount_job3ActionPerformed

    private void testcount_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testcount_job3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_testcount_job3ActionPerformed

    private void open_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open_job1ActionPerformed
        // TODO add your handling code here:
        if (pump_Open.isEnabled()) {
            JOptionPane.showMessageDialog(null, "请先打开水泵！");
        } else {
            DOdataSubmit_job1_manual test = new DOdataSubmit_job1_manual();

            if (open_job1.getText() == "打开") {
                open_job1.setText("关闭");
                test.start();
            } else {
                open_job1.setText("打开");
            }
        }

    }//GEN-LAST:event_open_job1ActionPerformed

    private void open_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open_job2ActionPerformed
        // TODO add your handling code here:
        if (pump_Open.isEnabled()) {
            JOptionPane.showMessageDialog(null, "请先打开水泵！");
        } else {
            DOdataSubmit_job2_manual test = new DOdataSubmit_job2_manual();

            if (open_job2.getText() == "打开") {
//                autorun_job2.setEnabled(false);
//                stop_job2.setEnabled(true);
                open_job2.setText("关闭");

                test.start();
            } else {
//                test.stop();
//                autorun_job2.setEnabled(true);
//                stop_job2.setEnabled(false);
                open_job2.setText("打开");
            }
        }
    }//GEN-LAST:event_open_job2ActionPerformed

    private void open_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open_job3ActionPerformed
        // TODO add your handling code here:
        if (pump_Open.isEnabled()) {
            JOptionPane.showMessageDialog(null, "请先打开水泵！");
        } else {
            DOdataSubmit_job3_manual test = new DOdataSubmit_job3_manual();

            if (open_job3.getText() == "打开") {
//                autorun_job3.setEnabled(false);
//                stop_job3.setEnabled(true);
                open_job3.setText("关闭");
                test.start();
            } else {
//                test.stop();
//                autorun_job3.setEnabled(true);
//                stop_job3.setEnabled(false);
                open_job3.setText("打开");
            }
        }
    }//GEN-LAST:event_open_job3ActionPerformed

    private void stop_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop_job1ActionPerformed
        // TODO add your handling code here:
        if (dosub_job1 != null) {
            dosub_job1.stop();
        }
        configbut_job1.setEnabled(true);
        stop_job1.setEnabled(false);
        open_job1.setEnabled(true);
        autorun_job1.setEnabled(true);
    }//GEN-LAST:event_stop_job1ActionPerformed

    private void stop_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop_job2ActionPerformed
        // TODO add your handling code here:
        if (dosub_job2 != null) {
            dosub_job2.stop();
        }
        configbut_job2.setEnabled(true);
        stop_job2.setEnabled(false);
        open_job2.setEnabled(true);
        autorun_job2.setEnabled(true);
    }//GEN-LAST:event_stop_job2ActionPerformed

    private void stop_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop_job3ActionPerformed
        // TODO add your handling code here:
        if (dosub_job3 != null) {
            dosub_job3.stop();
        }
        configbut_job3.setEnabled(true);
        stop_job3.setEnabled(false);
        open_job3.setEnabled(true);
        autorun_job3.setEnabled(true);
    }//GEN-LAST:event_stop_job3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        config_job1.show(false);
        write_job1();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        testtime_job1.setText(lastTesttime);
        maxpush_job1_top.setText(lastMaxpush);
        testcount_job1.setText(lastTestcount);
        // waterPressure_job1.setText(lastPumppressure);
        keeptime_job1.setText(lastKeeptime);
        config_job1.show(false);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        config_job3.show(false);
        write_job1();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        testtime_job3.setText(lastTesttime);
        maxpush_job3_top.setText(lastMaxpush);
        testcount_job3.setText(lastTestcount);
        //waterPressure_job3.setText(lastPumppressure);
        keeptime_job3.setText(lastKeeptime);
        config_job3.show(false);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void keeptime_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keeptime_job1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_keeptime_job1ActionPerformed

    private void keeptime_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keeptime_job3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_keeptime_job3ActionPerformed

    private void keeptime_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keeptime_job2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_keeptime_job2ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        testtime_job2.setText(lastTesttime);
        maxpush_job2_top.setText(lastMaxpush);
        testcount_job2.setText(lastTestcount);
        // waterPressure_job2.setText(lastPumppressure);
        keeptime_job2.setText(lastKeeptime);
        config_job2.show(false);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        config_job2.show(false);
        write_job1();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void testcount_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testcount_job2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_testcount_job2ActionPerformed

    private void entranceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entranceActionPerformed
        // TODO add your handling code here:
        // this.show();
        //welcome.show(false);

    }//GEN-LAST:event_entranceActionPerformed

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitActionPerformed

    private void testedCount_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testedCount_job1ActionPerformed
        // TODO add your handling code here:
        // currentCountJob1 = Integer.parseInt(testedCount_job1.getText());
    }//GEN-LAST:event_testedCount_job1ActionPerformed

    private void dynamic_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dynamic_ButtonActionPerformed
        // TODO add your handling code here:
//        if (dynamic_Button.getText() == "变频") {
//            DOData[0] = (byte) (DOData[0] | 0x02);//变频输出1
//        } else {
//            DOData[0] = (byte) (DOData[0] & 0x0D);//定频输出0
//        }
//        ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
//        if (Global.BioFaild(errorCode)) {
//            JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
//        } else {
//            if (dynamic_Button.getText() == "定频") {
//                dynamic_Button.setText("变频");
//                dynamic_Button.setToolTipText("此时为变频");
//            } else {
//                dynamic_Button.setText("定频");
//                dynamic_Button.setToolTipText("此时为定频");
//            }
//        }
        int port = 0;
        DOData[port] = (byte) (DOData[port] & 0xFD);
        ErrorCode errorCode = instantDoCtrl.Write(port, DOData[port]);
        if (Global.BioFaild(errorCode)) {
            ShowMessage("Sorry, there're some errors occred, ErrorCode: 2" + errorCode.toString());
        }
        static_Button.setEnabled(true);
        dynamic_Button.setEnabled(false);
        AOData[0] = Double.parseDouble(pumpPressure.getText());//获取模拟量输出数值，做数值转换。
        AOData[1] = 0;
        errorCode = instantAoCtrl.Write(0, 2, AOData);//输出模拟量信号        
        //System.out.printf("the number is :" + dataScaled[0]);
        if (Global.BioFaild(errorCode)) {
            JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
        }
    }//GEN-LAST:event_dynamic_ButtonActionPerformed
    protected void ShowMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "提示",
                JOptionPane.PLAIN_MESSAGE);
    }
    private void pump_OpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pump_OpenActionPerformed
        // TODO add your handling code here:    

        DOData[0] = (byte) (DOData[0] | 0x01);//将开启位设置成1
        if (dynamic_Button.isEnabled()) {//变频按钮可用说明此时状态为定频
            DOData[0] = (byte) (DOData[0] | 0x02);//定频输出1
            AOData[0] = avg();//从模拟输入通道4得到电压值
        AOData[1] = 0;
        // setpressure.setText(String.valueOf(frequency));
      ErrorCode  errorCode = instantAoCtrl.Write(0, 2, AOData);//输出模拟量信号        
        //System.out.printf("the number is :" + dataScaled[0]);
        if (Global.BioFaild(errorCode)) {
            JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
        }
        } else {
            DOData[0] = (byte) (DOData[0] & 0xFD);//变频输出0
            AOData[0] = Double.parseDouble(pumpPressure.getText());//获取模拟量输出数值，做数值转换。
            AOData[1] = 0;
            ErrorCode errorCode = instantAoCtrl.Write(0, 2, AOData);//输出模拟量信号        
            //System.out.printf("the number is :" + dataScaled[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            }

        }
        ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
        if (Global.BioFaild(errorCode)) {
            JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
        } else {

            AOData[0] = Double.parseDouble(pumpPressure.getText());//获取模拟量输出数值，做数值转换。
            AOData[1] = 0;
            errorCode = instantAoCtrl.Write(0, 2, AOData);//输出模拟量信号        

            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            }
            DItimer = new Timer();
            DItimer.schedule(new DIDataRecieve(), 0, 150);
            flag = true;
            //pumpPressure.setEditable(false);
            pump_Open.setEnabled(false);
            pump_Close.setEnabled(true);
            //dynamic_Button.setEnabled(false);
        }
    }//GEN-LAST:event_pump_OpenActionPerformed

    private void pump_CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pump_CloseActionPerformed
        // TODO add your handling code here:
        if (!autorun_job1.isEnabled() || !autorun_job2.isEnabled() || !autorun_job3.isEnabled()) {
            JOptionPane.showMessageDialog(null, "有工位正在自动运行，请勿关闭水泵！");
        } else {
            //dynamic_Button.setEnabled(true);
            DOData[0] = (byte) (DOData[0] & 0xFE);//将开启位设置成0
            ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            } else {
                //AItimer.cancel();
                //DItimer.cancel();
                pumpPressure.setEditable(true);
                pump_Open.setEnabled(true);
                pump_Close.setEnabled(false);

                DOData[0] = (byte) (DOData[0] & 0xDF);//job1 供水关闭
                errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                } else {
                    watersup_job1.setText("供水开启");
                }
                DOData[0] = (byte) (DOData[0] & 0xBF);//job2 供水关闭
                errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                } else {
                    watersup_job2.setText("供水开启");
                }
                DOData[0] = (byte) (DOData[0] & 0x7F);//job3 供水关闭
                errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                } else {
                    watersup_job3.setText("供水开启");
                }
            }
        }
    }//GEN-LAST:event_pump_CloseActionPerformed

    private void pumpPressureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pumpPressureActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_pumpPressureActionPerformed

    private void reset_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset_job1ActionPerformed
        // TODO add your handling code here:
        autorun_job1.setEnabled(true);
        warning_job1.setText("报警信息：");
        testedCount_job1.setText("0");
        waterpressure_job1.setText("0");
        realtimePressure_job1.setText("0");
        //testtime_job1.setText("0");
        // maxpush_job1.setText("0");
        // testcount_job1.setText("0");
        // keeptime_job1.setText("0");
    }//GEN-LAST:event_reset_job1ActionPerformed

    private void reset_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset_job2ActionPerformed
        // TODO add your handling code here:
        autorun_job2.setEnabled(true);
        warning_job2.setText("报警信息：");
        testedCount_job2.setText("0");
        waterpressure_job2.setText("0");
        realtimePressure_job2.setText("0");
        // testtime_job2.setText("0");
        // maxpush_job2.setText("0");
        // testcount_job2.setText("0");
        // keeptime_job2.setText("0");
    }//GEN-LAST:event_reset_job2ActionPerformed

    private void reset_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset_job3ActionPerformed
        autorun_job2.setEnabled(true);        // TODO add your handling code here:
        warning_job3.setText("报警信息：");
        testedCount_job3.setText("0");
        waterpressure_job3.setText("0");
        realtimePressure_job3.setText("0");
        // testtime_job3.setText("0");
        //  maxpush_job3.setText("0");
        // testcount_job3.setText("0");
        // keeptime_job3.setText("0");

    }//GEN-LAST:event_reset_job3ActionPerformed

    private void watersup_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_watersup_job1ActionPerformed
        // TODO add your handling code here:
        if (watersup_job1.getText() == "供水开启") {
            if (pump_Open.isEnabled()) {
                JOptionPane.showMessageDialog(null, "请先打开水泵！");
            } else {

                DOData[0] = (byte) (DOData[0] | 0x20);//job1 供水开启
                ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                } else {
                    watersup_job1.setText("供水关闭");
                }
            }
        } else {
            DOData[0] = (byte) (DOData[0] & 0xDF);//job1 供水关闭
            ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            } else {
                watersup_job1.setText("供水开启");
            }
        }
    }//GEN-LAST:event_watersup_job1ActionPerformed

    private void watersup_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_watersup_job2ActionPerformed
        // TODO add your handling code here:
        if (watersup_job2.getText() == "供水开启") {
            if (pump_Open.isEnabled()) {
                JOptionPane.showMessageDialog(null, "请先打开水泵！");
            } else {

                DOData[0] = (byte) (DOData[0] | 0x40);//job2 供水开启
                ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                } else {
                    watersup_job2.setText("供水关闭");
                }
            }
        } else {
            DOData[0] = (byte) (DOData[0] & 0xBF);//job2 供水关闭
            ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            } else {
                watersup_job2.setText("供水开启");
            }
        }
    }//GEN-LAST:event_watersup_job2ActionPerformed

    private void watersup_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_watersup_job3ActionPerformed
        // TODO add your handling code here:
        if (watersup_job3.getText() == "供水开启") {
            if (pump_Open.isEnabled()) {
                JOptionPane.showMessageDialog(null, "请先打开水泵！");
            } else {

                DOData[0] = (byte) (DOData[0] | 0x80);//job3 供水开启
                ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
                if (Global.BioFaild(errorCode)) {
                    JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
                } else {
                    watersup_job3.setText("供水关闭");
                }
            }
        } else {
            DOData[0] = (byte) (DOData[0] & 0x7F);//job3 供水关闭
            ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            } else {
                watersup_job3.setText("供水开启");
            }
        }
    }//GEN-LAST:event_watersup_job3ActionPerformed

    private void watersup_job1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_watersup_job1StateChanged
        // TODO add your handling code here:

    }//GEN-LAST:event_watersup_job1StateChanged

    private void watersup_job1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_watersup_job1ItemStateChanged

    }//GEN-LAST:event_watersup_job1ItemStateChanged

    private void maxpush_job1_topActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxpush_job1_topActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxpush_job1_topActionPerformed

    private void pumpPressureFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pumpPressureFocusLost
        // TODO add your handling code here:

    }//GEN-LAST:event_pumpPressureFocusLost

    private void pumpPressureFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pumpPressureFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_pumpPressureFocusGained

    private void modifyCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyCancelActionPerformed
        // TODO add your handling code here:
        SetPumpPresure.show(false);
    }//GEN-LAST:event_modifyCancelActionPerformed

    private void modifyOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyOKActionPerformed
        // TODO add your handling code here:

        AOData[0] = Double.parseDouble(modifypresure.getText());//获取模拟量输出数值，做数值转换。
        AOData[1] = 0;
        ErrorCode errorCode = instantAoCtrl.Write(0, 2, AOData);//输出模拟量信号          
        System.out.printf("the number is :" + AOData[0]);
        if (Global.BioFaild(errorCode)) {
            JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
        }
        pumpPressure.setText(modifypresure.getText());
        SetPumpPresure.show(false);
    }//GEN-LAST:event_modifyOKActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        modifypresure.setText(pumpPressure.getText());
        SetPumpPresure.show(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void switch_job1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switch_job1ActionPerformed
        // TODO add your handling code here:

        //DOData[0] = (byte) (DOData[0] & 0xFB);
        //DOData[0] = (byte) (DOData[0] | 0x04);
        if (switch_job1.getText() == "向下") {
            switch_job1.setText("向上");
            DOData[0] = (byte) (DOData[0] | 0x04);
            ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            }
        } else {
            switch_job1.setText("向下");
            DOData[0] = (byte) (DOData[0] & 0xFB);
            ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            }
        }
    }//GEN-LAST:event_switch_job1ActionPerformed

    private void switch_job2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switch_job2ActionPerformed
        // TODO add your handling code here:

        if (switch_job2.getText() == "向下") {
            switch_job2.setText("向上");
            DOData[0] = (byte) (DOData[0] | 0x08);
            ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            }
        } else {
            switch_job2.setText("向下");
            DOData[0] = (byte) (DOData[0] & 0xF7);
            ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            }
        }
    }//GEN-LAST:event_switch_job2ActionPerformed

    private void switch_job3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switch_job3ActionPerformed
        // TODO add your handling code here:

        if (switch_job3.getText() == "向下") {
            switch_job3.setText("向上");
            DOData[0] = (byte) (DOData[0] | 0x10);
            ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            }
        } else {
            switch_job3.setText("向下");
            DOData[0] = (byte) (DOData[0] & 0xEF);
            ErrorCode errorCode = instantDoCtrl.Write(0, DOData[0]);
            if (Global.BioFaild(errorCode)) {
                JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
            }
        }
    }//GEN-LAST:event_switch_job3ActionPerformed

    private void adjustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adjustActionPerformed
        // TODO add your handling code here:
        read_adjust();
        adjust_Dialog.show(true);

    }//GEN-LAST:event_adjustActionPerformed

    private void push_adjust1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_push_adjust1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_push_adjust1ActionPerformed

    private void push_adjust2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_push_adjust2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_push_adjust2ActionPerformed

    private void push_adjust3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_push_adjust3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_push_adjust3ActionPerformed

    private void waterpressure_adjustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterpressure_adjustActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waterpressure_adjustActionPerformed

    private void water_adjustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_water_adjustActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_water_adjustActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        write_adjust();
        read_adjust();
        adjust_Dialog.show(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        adjust_Dialog.show(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void static_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_static_ButtonActionPerformed
        // TODO add your handling code here:
        int port = 0;

        DOData[port] = (byte) (DOData[port] | 0x02);
        ErrorCode errorCode = instantDoCtrl.Write(port, DOData[port]);
        if (Global.BioFaild(errorCode)) {
            ShowMessage("Sorry, there're some errors occred, ErrorCode: 2" + errorCode.toString());
        }
        static_Button.setEnabled(false);
        dynamic_Button.setEnabled(true);
        AOData[0] = avg();//从模拟输入通道4得到电压值
        AOData[1] = 0;
        // setpressure.setText(String.valueOf(frequency));
        errorCode = instantAoCtrl.Write(0, 2, AOData);//输出模拟量信号        
        //System.out.printf("the number is :" + dataScaled[0]);
        if (Global.BioFaild(errorCode)) {
            JOptionPane.showMessageDialog(null, "Sorry, there're some errors occred, ErrorCode: " + errorCode.toString());
        }
    }//GEN-LAST:event_static_ButtonActionPerformed

    private void maxpush_job1_downActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxpush_job1_downActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxpush_job1_downActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
//TODO exception
        }
        try {
//设置本属性将改变窗口边框样式定义
            BeautyEyeLNFHelper.frameBorderStyle
                    = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
//TODO exception
        }
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(water_3job.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(water_3job.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(water_3job.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(water_3job.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                water_3job w = new water_3job();
                w.setVisible(true);
                w.setResizable(false);
                // welcome.show();
            }
        });
    }

    //private static AIDataRecieve ai =new AIDataRecieve();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog SetPumpPresure;
    private javax.swing.JButton adjust;
    private javax.swing.JDialog adjust_Dialog;
    private javax.swing.JButton autorun_job1;
    private javax.swing.JButton autorun_job2;
    private javax.swing.JButton autorun_job3;
    private javax.swing.JLabel bianpinqi;
    private javax.swing.JDialog config_job1;
    private javax.swing.JDialog config_job2;
    private javax.swing.JDialog config_job3;
    private javax.swing.JButton configbut_job1;
    private javax.swing.JButton configbut_job2;
    private javax.swing.JButton configbut_job3;
    private javax.swing.JButton dynamic_Button;
    private javax.swing.JButton entrance;
    private javax.swing.JButton exit;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel job1;
    private javax.swing.JPanel job2;
    private javax.swing.JPanel job3;
    private javax.swing.JTextField keeptime_job1;
    private javax.swing.JTextField keeptime_job2;
    private javax.swing.JTextField keeptime_job3;
    private javax.swing.JLabel max_push;
    private javax.swing.JLabel max_push1;
    private javax.swing.JLabel max_push2;
    private javax.swing.JLabel max_push3;
    private javax.swing.JLabel max_push4;
    private javax.swing.JLabel max_push5;
    private javax.swing.JLabel max_push6;
    private javax.swing.JLabel max_push7;
    private javax.swing.JLabel max_push8;
    private javax.swing.JTextField maxpush_job1_down;
    private javax.swing.JTextField maxpush_job1_top;
    private javax.swing.JTextField maxpush_job2_down;
    private javax.swing.JTextField maxpush_job2_top;
    private javax.swing.JTextField maxpush_job3_down;
    private javax.swing.JTextField maxpush_job3_top;
    private javax.swing.JButton modifyCancel;
    private javax.swing.JButton modifyOK;
    private javax.swing.JTextField modifypresure;
    private javax.swing.JButton open_job1;
    private javax.swing.JButton open_job2;
    private javax.swing.JButton open_job3;
    private javax.swing.JTextField pumpPressure;
    private javax.swing.JButton pump_Close;
    private javax.swing.JButton pump_Open;
    private javax.swing.JTextField push_adjust1;
    private javax.swing.JTextField push_adjust2;
    private javax.swing.JTextField push_adjust3;
    private javax.swing.JTextField realtimePressure_job1;
    private javax.swing.JTextField realtimePressure_job2;
    private javax.swing.JTextField realtimePressure_job3;
    private javax.swing.JButton reset_job1;
    private javax.swing.JButton reset_job2;
    private javax.swing.JButton reset_job3;
    private javax.swing.JButton static_Button;
    private javax.swing.JButton stop_job1;
    private javax.swing.JButton stop_job2;
    private javax.swing.JButton stop_job3;
    private javax.swing.JButton switch_job1;
    private javax.swing.JButton switch_job2;
    private javax.swing.JButton switch_job3;
    private javax.swing.JPanel system;
    private javax.swing.JLabel temperature;
    private javax.swing.JLabel test_count;
    private javax.swing.JLabel test_count1;
    private javax.swing.JLabel test_count2;
    private javax.swing.JLabel test_time;
    private javax.swing.JLabel test_time1;
    private javax.swing.JLabel test_time2;
    private javax.swing.JTextField testcount_job1;
    private javax.swing.JTextField testcount_job2;
    private javax.swing.JTextField testcount_job3;
    private javax.swing.JTextField testedCount_job1;
    private javax.swing.JTextField testedCount_job2;
    private javax.swing.JTextField testedCount_job3;
    private javax.swing.JTextField testtime_job1;
    private javax.swing.JTextField testtime_job2;
    private javax.swing.JTextField testtime_job3;
    private javax.swing.JLabel warning_job1;
    private javax.swing.JLabel warning_job2;
    private javax.swing.JLabel warning_job3;
    private javax.swing.JTextField water_adjust;
    private javax.swing.JTextField waterpressure_adjust;
    private javax.swing.JTextField waterpressure_job1;
    private javax.swing.JTextField waterpressure_job2;
    private javax.swing.JTextField waterpressure_job3;
    private javax.swing.JButton watersup_job1;
    private javax.swing.JButton watersup_job2;
    private javax.swing.JButton watersup_job3;
    private static javax.swing.JDialog welcome;
    // End of variables declaration//GEN-END:variables

}
