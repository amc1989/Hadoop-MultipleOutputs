package com.axon.cloud.cityrecords.cityrecordsdays;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.axon.cloud.cityrecords.bean.CityInfo;



public class CityRecordsDaysReducer extends Reducer<Text, CityInfo, Text, Text> {
	// private MultipleOutputs mos;
	private MultipleOutputs mostransfer;
	private MultipleOutputs moscityrecords;
	private MultipleOutputs moscityhasrecordsday;

	@Override
	protected void setup(Reducer<Text, CityInfo, Text, Text>.Context context)
			throws IOException, InterruptedException {
		// mos = new MultipleOutputs(context);
		mostransfer = new MultipleOutputs(context);
		moscityrecords = new MultipleOutputs(context);
		moscityhasrecordsday = new MultipleOutputs(context);
	}

	@Override
	protected void reduce(Text text, Iterable<CityInfo> values,
			Reducer<Text, CityInfo, Text, Text>.Context context)
			throws IOException, InterruptedException {
		int i=1/0;
		HashMap<String, HashSet<String>> cityhasrecordsday = new HashMap<String, HashSet<String>>();
		HashSet<String> cityrecords = new HashSet<String>();
		TreeSet<String> cityretransferCounts = new TreeSet<String>();
		for (CityInfo ci : values) {
			String date = ci.getDate();
			String city = ci.getCityName();
			// 统计每个电话号码，出现的城市个数。
			cityrecords.add(city);
			// 统计每个电话号码城市间转移的次数
			cityretransferCounts.add(date+"-"+city);
			// 统计每个电话号码每个城市出现的天数
			HashSet<String> citydays = cityhasrecordsday.get(city);
			if (null == citydays) {
				citydays = new HashSet<String>();
			}
			citydays.add(date);
			cityhasrecordsday.put(city, citydays);
		}
		//context.write(text, new Text(cityrecords.size()+":"+cityrecords.toString()));
		// 打印每个电话号码，出现的城市个数。
		printCityRecords(cityrecords, text);


		// 打印每个电话号码每个城市出现的天数
		printCityHasRecordsDay(cityhasrecordsday, text);

		// 打印每个电话号码城市间转移的次数
		printCityRetransferCounts(cityretransferCounts, text);
	}

	// 打印每个电话号码城市间转移的次数
	private void printCityRetransferCounts(
			TreeSet<String> cityretransferCounts, Text text)
			throws IOException, InterruptedException {
		ArrayList<String> al = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();
		int count = 0;
		for(String s:cityretransferCounts){
			String[] str = s.split("-");
			al.add(str[1]);
		}
		for (int i = 0; i < al.size() - 1; i++) {
			String a = al.get(i);
			String b = al.get(i + 1);
			if (!(a.equals(b))) {
				count++;
				result.add(a + "-" + b);
			}

		}
		mostransfer.write("citytransfercount", text, new Text(count + ":"
				+ result.toString()));
	}

	// 打印每个电话号码每个城市出现的天数

	@SuppressWarnings("unchecked")
	private void printCityHasRecordsDay(
			HashMap<String, HashSet<String>> cityhasrecordsday, Text text)
			throws IOException, InterruptedException {
		Set<Map.Entry<String, HashSet<String>>> set = cityhasrecordsday
				.entrySet();
		for (Iterator<Map.Entry<String, HashSet<String>>> it = set.iterator(); it
				.hasNext();) {
			Map.Entry<String, HashSet<String>> me = it.next();
			String city = me.getKey();
			HashSet<String> days = me.getValue();
			moscityhasrecordsday.write("cityhasrecordsday", text, new Text(city
					+ ":" + days.size()+":"+days.toString()));

		}

	}

	// 打印每个电话号码，出现的城市个数。
	@SuppressWarnings("unchecked")
	private void printCityRecords(HashSet<String> cityrecords, Text text)
			throws IOException, InterruptedException {
		moscityrecords.write("cityrecords", text,
				new Text(cityrecords.size()+":"+cityrecords.toString()));

	}

}
