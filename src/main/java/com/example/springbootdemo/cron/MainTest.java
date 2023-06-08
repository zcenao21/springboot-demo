package com.example.springbootdemo.cron;

import com.example.springbootdemo.util.DateUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class MainTest {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {

            //创建一个jobDetail的实例，将该实例与HelloJob Class绑定
            JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity("myJob").build();
            //创建一个Trigger触发器的实例，定义该job立即执行，并且每2秒执行一次，一直执行
            SimpleTrigger trigger = TriggerBuilder.
                    newTrigger().
                    withIdentity("myTrigger").
                    startNow().
                    endAt(DateUtil.strToDateLong("2023-04-23 20:52:00")).
                    withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(2).repeatForever()).build();
            //创建schedule实例
            StdSchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();
            scheduler.start();
            scheduler.scheduleJob(jobDetail,trigger);
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            System.out.println(e.toString());
        }
    }

}
