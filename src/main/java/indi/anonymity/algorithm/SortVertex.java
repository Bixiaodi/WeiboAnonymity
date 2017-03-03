//package indi.anonymity.algorithm;
//
//import indi.anonymity.elements.Vertex;
//
//import java.text.Collator;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Locale;
//
///**
// * Created by emily on 17/2/15.
// */
//public class SortVertex {
//    public void sort(ArrayList<Vertex> vertex) {
//        Collections.sort(vertex, new Comparator<Vertex>() {
//            @Override
//            public int compare(Vertex o1, Vertex o2) {
//                if(o1.getGender() == o2.getGender()) {
//                    int location = Collator.getInstance(Locale.CHINA).compare(o1.getLocation(), o2.getLocation());
//                //    int location = o1.getLocation().compareTo(o2.getLocation());
//                    if(location == 0) {
//                        int description = Collator.getInstance(Locale.CHINA).compare(o1.getDescription(), o2.getDescription());
//                //        int description = o1.getDescription().compareTo(o2.getDescription());
//                        if(description == 0) {
//                            int userTag = Collator.getInstance(Locale.CHINA).compare(o1.getUserTag(), o2.getUserTag());
//                //            int userTag = o1.getUserTag().compareTo(o2.getUserTag());
//                            return userTag;
//                        } else {
//                            return description;
//                        }
//                    } else {
//                        return location;
//                    }
//                } else {
//                    return o1.getGender() - o2.getGender();
//                }
//            }
//        });
//    }
//}
