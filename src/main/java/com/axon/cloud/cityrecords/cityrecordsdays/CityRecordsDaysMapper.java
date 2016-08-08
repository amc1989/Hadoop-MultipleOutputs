package com.axon.cloud.cityrecords.cityrecordsdays;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.axon.cloud.cityrecords.bean.CityInfo;





public class CityRecordsDaysMapper extends
        Mapper<LongWritable, Text, Text, CityInfo> {
	private MultipleOutputs mos;
	int count =0;
    private HashMap<String, String> hm = new HashMap<String, String>();
    //private HashMap<String, String> hm = new HashMap<String, String>();
  //  private MultipleOutputs mos;
   
    Log  log = LogFactory.getLog(CityRecordsDaysMapper.class);
    //900011307349203232    53648   54402   1448527658  2
    //   手机号                                                       LAC      CI       date
    //    0                   1        2         3
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
        count++;
        if(count>1000) return;
        String[] cityName_date_telNum = value.toString().split("\\s");
        CityInfo cityInfo = new CityInfo();
        String date = DateFormatUtils.format(Long.parseLong(cityName_date_telNum[3]+"000"),
                "yyyyMMdd");
        String cityName = hm.get(cityName_date_telNum[1]);
        if (null == cityName)
            return ;
        //  public void set(String cityName, String date, String telNum)
        cityInfo.set(cityName, date, cityName_date_telNum[0]);
        context.write(new Text(cityName_date_telNum[0]),cityInfo );
    }

    @Override
    protected void setup(Context context) throws IOException,
            InterruptedException {
    	 mos = new MultipleOutputs(context);
        InputStream in = this.getClass().getResourceAsStream(
                "/META-INF/laglnc.txt");
        List<String> list = IOUtils.readLines(in);
        System.out.println(list);
        for (String s : list) {
            String[] str = s.split(",");
            hm.put(str[0], str[1]);
            mos.write("lacandci111", new Text(str[0]), new Text(str[1]));
            log.info(str[0] + ":" + str[1]);
        }
        in.close();
    }
    public void DU() throws IOException{
    	InputStream in = this.getClass().getResourceAsStream(
                "/META-INF/laglnc.txt");
         List<String> list = IOUtils.readLines(in);
        System.out.println(list);
    }
    public static void main(String [] args) throws IOException {
    	CityRecordsDaysMapper cm =  new CityRecordsDaysMapper();
    	cm.DU();
    }
}
