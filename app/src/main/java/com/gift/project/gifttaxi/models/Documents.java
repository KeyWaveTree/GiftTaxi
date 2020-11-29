package com.gift.project.gifttaxi.models;

import com.google.api.client.util.Key;

public class Documents {
    @Key("road_address") public RoadAddress roadaddress;
    //@Key() public Address address;
    @Key("address") public Address address;
    @Key("Y") public String latitude;
    @Key("X") public String longitude;
}
