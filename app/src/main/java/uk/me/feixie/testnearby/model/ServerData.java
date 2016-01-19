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
        public ArrayList<ResJapanKorean> resJapanKorean;
        public ArrayList<ResVenTai> resVenTai;
        public ArrayList<ResWestern> resWestern;
        public ArrayList<ResIndiaTurkey> resIndiaTurkey;
        public ArrayList<Tea> tea;
        public ArrayList<Coffee> coffee;
        public ArrayList<Desert> desert;


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

        public class ResJapanKorean implements Serializable {
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
                return "ResJapanKorean{" +
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

        public class ResVenTai implements Serializable {
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
                return "ResVenTai{" +
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

        public class ResWestern implements Serializable {
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
                return "ResWestern{" +
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

        public class ResIndiaTurkey implements Serializable {
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
                return "ResIndiaTurkey{" +
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

        public class Tea implements Serializable {
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
                return "Tea{" +
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

        public class Coffee implements Serializable {
            public String id;
            public String name;
            public String category;
            public String url;
            public String address;
            public String postcode;
            public String tel;

            @Override
            public String toString() {
                return "Coffee{" +
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

        public class Desert implements Serializable {
            public String id;
            public String name;
            public String category;
            public String url;
            public String address;
            public String postcode;
            public String tel;

            @Override
            public String toString() {
                return "Desert{" +
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


