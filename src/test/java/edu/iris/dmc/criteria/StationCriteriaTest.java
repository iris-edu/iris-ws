package edu.iris.dmc.criteria;

import org.junit.Test;

public class StationCriteriaTest {

    @Test
    public void m()throws Exception{
        Criteria criteria=StationCriteria.builder().chanCode("BHZ").build();
        System.out.println(criteria.toUrlParams());
        System.out.println(criteria.toMapUrlParameters());
    }
}
