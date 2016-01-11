package uk.me.feixie.testnearby.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Fei on 08/01/2016.
 */
public class ServerData implements Serializable {

    public ArrayList<DataItem> data;

    @Override
    public String toString() {
        return "ServerData{" +
                "data=" + data +
                '}';
    }

    public class DataItem implements Serializable {
        public String id;
        public String title;
        public ArrayList<Restaurant> restruant;

        @Override
        public String toString() {
            return "DataItem{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", restruant=" + restruant +
                    '}';
        }

        public class Restaurant implements Serializable {
            //{"id":"1","name":"Chi Kitchen", "category":"Chinese", "url":"http://www.honglingjin.co.uk/168422.html",
            // "address":"334-348 Oxford Street, London", "postcode":"W1C 1JG", "tel":"020 3841 6888"}
            public String id;
            public String name;
            public String category;
            public String url;
            public String address;
            public String postcode;
            public String tel;

            @Override
            public String toString() {
                return "Restaurant{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", category='" + category + '\'' +
                        ", url='" + url + '\'' +
                        ", address='" + address + '\'' +
                        ", postcode='" + postcode + '\'' +
                        ", tel='" + tel + '\'' +
                        '}';
            }
        }
    }




}


