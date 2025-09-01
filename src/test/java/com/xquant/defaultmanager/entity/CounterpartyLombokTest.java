package com.xquant.defaultmanager.entity;

public class CounterpartyLombokTest {
    public static void main(String[] args) {
        Counterparty cp = new Counterparty();
        cp.setCounterpartyCode("TEST001");
        System.out.println("Counterparty Code: " + cp.getCounterpartyCode());
        System.out.println("Lombok 测试成功！");
    }
}
