package bj.justpingit;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BaiJiFeiLong@gmail.com at 2018/5/12 13:00
 */
public class App {
    public static void main(String[] args) throws IOException, InterruptedException {

        List<Date> keyList = new LinkedList<>();
        List<Integer> valueList = new LinkedList<>();

        XYChart chart = new XYChartBuilder().width(400).height(200).build();
        SwingWrapper<XYChart> wrapper = new SwingWrapper<>(chart);
        wrapper.displayChart("JustPingIt");
        Thread.sleep(100);

        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String command = String.format("ping -%s 1000000 www.baidu.com", isWindows ? "n" : "c");
        Process process = Runtime.getRuntime().exec(command);
        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "GBK");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        Pattern pattern = Pattern.compile(".*?([\\d.]+)\\s?ms.*");
        while ((line = bufferedReader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                int ms = Integer.parseInt(matcher.group(1));
                keyList.add(new Date());
                valueList.add(ms);
                while (keyList.size() > 100) {
                    ((LinkedList<Date>) keyList).removeFirst();
                    ((LinkedList<Integer>) valueList).removeFirst();
                }
                if (!chart.getSeriesMap().containsKey("baidu")) {
                    chart.addSeries("baidu", keyList, valueList);
                } else {
                    chart.updateXYSeries("baidu", keyList, valueList, null);
                }
                SwingUtilities.invokeLater(wrapper::repaintChart);
            }
        }
    }
}
