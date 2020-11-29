package com.gift.project.gifttaxi.Dto;

import com.google.api.client.util.Key;

public class EstimateResultDto {
    @Key()public double estimateDistance;
    @Key()public int estimateCost;
    @Key()public int estimateTime;
}
