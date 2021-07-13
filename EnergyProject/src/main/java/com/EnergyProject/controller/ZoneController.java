package com.EnergyProject.controller;

import com.EnergyProject.dao.ZoneDAO;
import com.EnergyProject.dao.ZoneDAOForNoCallable;
import com.EnergyProject.pojo.Zone;
import com.EnergyProject.server.ProAmountServer;
import com.EnergyProject.server.ZoneServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ZoneController {
    @Autowired
    private ZoneServer zoneServer;

    @Autowired
    private ZoneDAO zoneDAO;
    @Autowired
    private ProAmountServer proAmountServer;
    @Autowired
    private ZoneDAOForNoCallable zoneDAOForNoCallable;
    private Map<String,Object> frontEndData=new HashMap<>();
    private Map<String,Object> yesterdayFrontEndData=new HashMap<>();
    /*@GetMapping("/getYesterdayData")
    public Map<String, Object> getYesterdayData(@RequestParam(value = "isAmount",defaultValue ="false") Boolean isAmount) throws ParseException {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            frontEndData.remove("init");
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(-1);
        int year = localDateTime.getYear();
        int monthValue = localDateTime.getMonthValue();
        int dayOfMonth = localDateTime.getDayOfMonth();
        stringObjectHashMap.put("starttime", LocalDateTime.of(year,6,27,0,0,0));
        stringObjectHashMap.put("eid", 26);
        stringObjectHashMap.put("endTime", LocalDateTime.of(year,6,27,23,30,0));
        stringObjectHashMap.put("addtime", 1);
        List<Zone> zoneList = zoneServer.getzonelistForCUROS(stringObjectHashMap);
        frontEndData.put("main",zoneList);

            stringObjectHashMap.put("starttime", LocalDateTime.of(year,6, 28,0,0,0));
            stringObjectHashMap.put("endTime", LocalDateTime.of(year,6,28,0,30,0));
            List<Zone> zone = zoneServer.getzonelistForCUROS(stringObjectHashMap);
            frontEndData.put("init",zone);
            if (isAmount)
            {
               zoneServer.hoursAmount(frontEndData,year,monthValue,dayOfMonth,true,5);
            }

        return frontEndData;
    }*/

    @GetMapping("/getYesterdayData")
    public Map<String, Object> getYesterdayData(@RequestParam(value = "isAmount",defaultValue ="true") Boolean isAmount,@RequestParam(value = "isHours",defaultValue = "hours") String Hours) throws ParseException {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(-1);
        int year = localDateTime.getYear();
        int monthValue = localDateTime.getMonthValue();
        int dayOfMonth = localDateTime.getDayOfMonth();
        zoneServer.handlerDateOfAmount(frontEndData,year,6,27,true,Hours,null);
        if (isAmount)
        {
            zoneServer.hoursAmount(frontEndData,year,6,27,Hours,true,5);
        }

        return frontEndData;
    }

    @GetMapping("/getNowDayData")
    public Map<String,Object> getNowDayData(@RequestParam(value = "isAmount",defaultValue ="true") Boolean isAmount,@RequestParam(value = "isTotal",defaultValue ="true") Boolean isTotal,@RequestParam(value = "selectMode",defaultValue = "hours") String selectMode)
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        int year = localDateTime.getYear();
        int monthValue = localDateTime.getMonthValue();
        int dayOfMonth = localDateTime.getDayOfMonth();
        zoneServer.handlerDateOfAmount(frontEndData,year,6,28,isTotal,selectMode,null);
        if (isAmount)
        {
            zoneServer.hoursAmount(frontEndData,year,6,28,selectMode,isTotal,5);
        }

        return frontEndData;
    }

    @PostMapping("/getTodayTotalAllData")
    public List<Zone> getTodayTotalAllData()
    {
        List<Zone> zones = zoneServer.selectTotalZone();
        IntSummaryStatistics collect = zones.stream().collect(Collectors.summarizingInt(Zone::gettValue));
        List<Zone> collectFiler = zones.stream().filter(value -> value.getEid() != 27).collect(Collectors.toList());
        collectFiler.add(new Zone(null,(int) collect.getSum(),collectFiler.get(0).gettTime()));

        return collectFiler;
    }
    @PostMapping("/getYesterdayAllData")
    public Map<String, Object> getYesterdayAllData()
    {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(-1);
        List<Zone> zoneYesterday = zoneServer.getselectTotalZoneForYesterday(27, 6, localDateTime.getYear());
        List<Zone> zoneBeforeDay = zoneServer.getselectTotalZoneForYesterday(26, 6, localDateTime.getYear());
        List<Zone> zoneLastMonth = zoneServer.getselectTotalZoneForYesterday(27, 5, localDateTime.getYear());
        IntSummaryStatistics collectYesterday = zoneYesterday.stream().collect(Collectors.summarizingInt(Zone::gettValue));
        IntSummaryStatistics collectBeforeDay = zoneBeforeDay.stream().collect(Collectors.summarizingInt(Zone::gettValue));
        IntSummaryStatistics collectLastMonth = zoneLastMonth.stream().collect(Collectors.summarizingInt(Zone::gettValue));
        List<Zone> collectFiler = zoneYesterday.stream().filter(value -> value.getEid() != 27).collect(Collectors.toList());
        collectFiler.add(new Zone(null,(int) collectYesterday.getSum(),collectFiler.get(0).gettTime()));
        Double beforeDayPercentage=null;
        Double lastMonthPercentage=null;
        if (collectYesterday.getSum()!=0 && collectBeforeDay.getSum()!=0)
        {
             beforeDayPercentage=(collectYesterday.getSum()-collectBeforeDay.getSum())/(collectBeforeDay.getSum()*1.0)*100;
        }
        if (collectYesterday.getSum()!=0 && collectLastMonth.getSum()!=0)
        {
            lastMonthPercentage=(collectYesterday.getSum()-collectLastMonth.getSum())/(collectLastMonth.getSum()*1.0)*100;
        }

        stringObjectHashMap.put("before",beforeDayPercentage);
        stringObjectHashMap.put("lastMonth",lastMonthPercentage);
        stringObjectHashMap.put("yesterdayData",collectFiler);
        System.out.println(stringObjectHashMap);
        return stringObjectHashMap;

    }


    @GetMapping("/getTesterdayDataForMonth")

    public Map<String, Object> getTesterdayDataForMonth() throws ParseException {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        LocalDateTime localDateTime = LocalDateTime.now().plusMonths(-1);
        int year = localDateTime.getYear();
        int monthValue = localDateTime.getMonthValue();
        Month month = localDateTime.getMonth();
        int maxDay = month.maxLength();
        int dayOfMonth = localDateTime.getDayOfMonth();
        stringObjectHashMap.put("starttime", LocalDateTime.of(year,monthValue,dayOfMonth,0,0,0));
        stringObjectHashMap.put("eid", 26);
        stringObjectHashMap.put("endTime", LocalDateTime.of(year,monthValue,maxDay,0,30,0));
        stringObjectHashMap.put("addtime", 1);
        List<Zone> zoneList = zoneServer.getzonelistForCUROSForDay(stringObjectHashMap);
        yesterdayFrontEndData.put("main",zoneList);
        stringObjectHashMap.put("starttime", LocalDateTime.of(year,monthValue+1,1,0,0,0));
        stringObjectHashMap.put("endTime", LocalDateTime.of(year,monthValue+1,1,23,0,0));
            List<Zone> zone = zoneServer.getzonelistForCUROSForDay(stringObjectHashMap);
            yesterdayFrontEndData.put("init",zone);


        return yesterdayFrontEndData;
    }

    @GetMapping("/getZoneAllNode")
    public List<Integer> getZoneAllNode()
    {
       return zoneServer.getZoneAllNode();
    }

    @GetMapping("/getNodeZoneData")
    public List<Zone> getNodeZoneData(Integer node, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime, String selectMode,String selectType)
    {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("starttime", startTime);
        stringObjectHashMap.put("eid", node);
        stringObjectHashMap.put("endTime",endTime);
        if ("minutes".equals(selectMode)){
            stringObjectHashMap.put("addtime", 302);
        }
        else
        {
            stringObjectHashMap.put("addtime", 1);
        }

        List<Zone> nodeZoneTotalData = zoneServer.getNodeZoneTotalData(stringObjectHashMap, selectMode);
        if ("total".equals(selectType))
        {
            return nodeZoneTotalData;
        }
        else
        {
            LocalDateTime lastDataTime=null;
            ArrayList<Zone> zones = new ArrayList<>();
            nodeZoneTotalData.forEach(iteam->{
                zones.add(new Zone(iteam.getEid(),iteam.gettValue(),iteam.gettTime()));
            });
            System.out.println(zones);
            Zone zone=null;
            if ("hours".equals(selectMode))
            {
               lastDataTime= startTime.plusHours(-1);
                 zone= zoneServer.getselectZoneForSpecificTime(lastDataTime.getDayOfMonth(), lastDataTime.getMonthValue(), lastDataTime.getYear(), node);

            }
            else if ("days".equals(selectMode))
            {
                lastDataTime=startTime.plusDays(-1);
                zone= zoneServer.getselectZoneForSpecificTime(lastDataTime.getDayOfMonth(), lastDataTime.getMonthValue(), lastDataTime.getYear(), node);
            }
            else
            {
                lastDataTime=startTime.plusSeconds(-302);
                zone= zoneServer.getselectZoneForSpecificTime(lastDataTime.getDayOfMonth(), lastDataTime.getMonthValue(), lastDataTime.getYear(), node);
            }
            for (int i=0;i<nodeZoneTotalData.size();i++)
            {
                if (i==0)
                {
                    zones.get(i).settValue(nodeZoneTotalData.get(i).gettValue()-zone.gettValue());
                }
                else
                {
                    zones.get(i).settValue(nodeZoneTotalData.get(i).gettValue()-nodeZoneTotalData.get(i-1).gettValue());
                }
            }
            return zones;
        }


    }
}
