package com.mechanitis.demo.sense.client.mood;

import com.mechanitis.demo.sense.service.MessageListener;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

import java.util.HashMap;
import java.util.Map;

import static java.time.LocalTime.now;
import static java.util.stream.IntStream.range;

public class HappinessChartData implements MessageListener {
    private final XYChart.Series<String, Double> dataSeries = new XYChart.Series<>();
    private final Map<Integer, Integer> minuteToDataPosition = new HashMap<>();

    public HappinessChartData() {
        int nowMinute = now().getMinute();
        range(nowMinute, nowMinute + 10).forEach(this::initialiseBarToZero);
    }

    private void initialiseBarToZero(int minute) {
        dataSeries.getData().add(new Data<>(String.valueOf(minute), 0.0));
        minuteToDataPosition.put(minute, dataSeries.getData().size() - 1);
    }

    @Override
    public void onMessage(String mood) {
        if (mood.equals("HAPPY")) {
            int x = now().getMinute();

            Integer dataIndex = minuteToDataPosition.get(x);
            Data<String, Double> barForNow = dataSeries.getData().get(dataIndex);
            barForNow.setYValue(barForNow.getYValue() + 1);
        }
    }

    XYChart.Series<String, Double> getDataSeries() {
        return dataSeries;
    }
}

