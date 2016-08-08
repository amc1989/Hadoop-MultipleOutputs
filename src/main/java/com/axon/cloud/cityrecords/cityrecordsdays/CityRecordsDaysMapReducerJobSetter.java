package com.axon.cloud.cityrecords.cityrecordsdays;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.axon.cloud.cityrecords.bean.CityInfo;
import com.axon.icloud.framework.hdfs.HdfsTemplate;
import com.axon.icloud.framework.mapreduce.MapReduceInterceptor;
import com.axon.icloud.framework.mapreduce.MapReduceJobSetter;
import com.axon.icloud.framework.mapreduce.MapReduceTemplate;

@Service(CityRecordsDaysMapReducerJobSetter.BEAN_ID)
@Scope("prototype")
public class CityRecordsDaysMapReducerJobSetter implements MapReduceJobSetter,
		MapReduceInterceptor {
	public static final String BEAN_ID = "cityrecordsdays";
	Log log = LogFactory.getLog(getClass());
	@Resource
	private HdfsTemplate hdfsTemplate;
	@Resource
	private MapReduceTemplate mapReduceTemplate;
	private String outputPath;

	@Override
	public void before() {
		outputPath = "/crdcsv1";
	
	}

	@Override
	public void set(Job job) throws IOException {
		log.info("开始执行");
		
		job.setJarByClass(CityRecordsDaysMapReducerJobSetter.class);

		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		FileInputFormat.addInputPath(job, new Path(
				"/location/location-201512.csv"));
        
		job.setMapperClass(CityRecordsDaysMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(CityInfo.class);
		

		job.setReducerClass(CityRecordsDaysReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		MultipleOutputs.addNamedOutput(job, "lacandci111", TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "cityhasrecordsday",
				TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "cityrecords",
				TextOutputFormat.class, Text.class, Text.class);
		MultipleOutputs.addNamedOutput(job, "citytransfercount",
				TextOutputFormat.class, Text.class, Text.class);

		job.setNumReduceTasks(15);
	}

	@Override
	public void after() {

		String hdfsUricityhasrecordsday = outputPath + "/"
				+ "cityhasrecordsday" + "-r-000";
		String hdfsUricityrecords = outputPath + "/" + "cityrecords"
				+ "-r-000";
		String hdfsUricitytransfercount = outputPath + "/"
				+ "citytransfercount" + "-r-000";

		OutputStream osCityhasrecordsday = null;
		InputStream isCityhasrecordsday = null;
		OutputStream osCityrecords = null;
		InputStream isCityrecords = null;
		OutputStream osCitytransfercount = null;
		InputStream isCitytransfercount = null;
		File cityhasrecordsday = null;
		File cityrecords = null;
		File citytransfercount = null;
		try {
			File dir = new File("./export1");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			for (int i = 0; i < 15; i++) {
				cityhasrecordsday = new File(dir, "cityhasrecordsday" + i
						+ ".csv");
				osCityhasrecordsday = new FileOutputStream(cityhasrecordsday);
				if (i < 10) {
					isCityhasrecordsday = hdfsTemplate
							.getInputStream(hdfsUricityhasrecordsday + "0" + i);
					log.info("HDFS isCityhasrecordsday: "
							+ isCityhasrecordsday.toString()
							+ " downloaded successful!");
					IOUtils.copy(isCityhasrecordsday, osCityhasrecordsday);

				} else {
					isCityhasrecordsday = hdfsTemplate
							.getInputStream(hdfsUricityhasrecordsday + i);
					log.info("HDFS isCityhasrecordsday: "
							+ isCityhasrecordsday.toString()
							+ " downloaded successful!");
					IOUtils.copy(isCityhasrecordsday, osCityhasrecordsday);
				}
			}

            for(int j=0;j<15;j++){
			cityrecords = new File(dir, "cityrecords"+j+ ".csv");
			osCityrecords = new FileOutputStream(cityrecords);
			if(j<10){
			isCityrecords = hdfsTemplate.getInputStream(hdfsUricityrecords+"0"+j);
			log.info("HDFS isCityrecords: " + isCityrecords.toString()
					+ " downloaded successful!");
			IOUtils.copy(isCityrecords, osCityrecords);
			}else {
				isCityrecords = hdfsTemplate.getInputStream(hdfsUricityrecords+j);
				log.info("HDFS isCityrecords: " + isCityrecords.toString()
						+ " downloaded successful!");
				IOUtils.copy(isCityrecords, osCityrecords);
				}
            }
            
            for(int m=0;m<15;m++){
			citytransfercount = new File(dir, "citytransfercount"+m+ ".csv");
			osCitytransfercount = new FileOutputStream(citytransfercount);
			if(m<10){
			isCitytransfercount = hdfsTemplate
					.getInputStream(hdfsUricitytransfercount+"0"+m);
			log.info("HDFS isCitytransfercount: "
					+ isCitytransfercount.toString()
					+ " downloaded successful!");
			IOUtils.copy(isCitytransfercount, osCitytransfercount);
			}else {
				isCitytransfercount = hdfsTemplate
						.getInputStream(hdfsUricitytransfercount+m);
				log.info("HDFS isCitytransfercount: "
						+ isCitytransfercount.toString()
						+ " downloaded successful!");
				IOUtils.copy(isCitytransfercount, osCitytransfercount);
			 }
            }
			log.info("HDFS file: " + hdfsUricityhasrecordsday
					+ " downloaded successful!");
			log.info("HDFS file: " + hdfsUricitytransfercount
					+ " downloaded successful!");
			log.info("HDFS file: " + hdfsUricityrecords
					+ " downloaded successful!");
		} catch (Exception e) {
			log.error("HDFS file: " + hdfsUricityrecords
					+ " downloaded failed!", e);
		} finally {
			IOUtils.closeQuietly(osCityhasrecordsday);
			IOUtils.closeQuietly(isCityhasrecordsday);
			IOUtils.closeQuietly(osCitytransfercount);
			IOUtils.closeQuietly(isCitytransfercount);
			IOUtils.closeQuietly(osCityrecords);
			IOUtils.closeQuietly(isCityrecords);
		}

	}

	@Override
	public void exception(Exception e) {
		// TODO Auto-generated method stub

	}

}
