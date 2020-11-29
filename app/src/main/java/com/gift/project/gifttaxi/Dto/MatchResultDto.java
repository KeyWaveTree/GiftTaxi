package com.gift.project.gifttaxi.Dto;

import com.google.api.client.util.Key;

public class MatchResultDto {
    @Key() public String taxiNumber;
    @Key() public String driver;
    @Key() public int arrivalTime;
}
