package com.hejunlin.liveplayback.bean;

import java.util.List;

public class ProgramBean {

    /**
     * typeId :
     * type : 1
     * typeName : 央视
     * items : [{"id":"","type":1,"name":"CCTV1","url":"http://cctvalih5ca.v.myalicdn.com/live/cctv1_2/index.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV2","url":"http://61.151.179.194/tlivecloud-cdn.ysp.cctv.cn/cctv/2000203601.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV3","url":"http://61.151.179.194/tlivecloud-cdn.ysp.cctv.cn/cctv/2000203801.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV4","url":"url","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV5","url":"http://61.151.179.194/tlivecloud-cdn.ysp.cctv.cn/cctv/2000205101.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV6","url":"http://61.151.179.194/tlivecloud-cdn.ysp.cctv.cn/cctv/2000203301.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV7","url":"http://61.151.179.194/tlivecloud-cdn.ysp.cctv.cn/cctv/2000510001.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV8","url":"http://61.151.179.194/tlivecloud-cdn.ysp.cctv.cn/cctv/2000203901.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV9","url":"url","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV10","url":"url","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV11","url":"http://61.151.179.194/tlivecloud-cdn.ysp.cctv.cn/cctv/2000204101.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV12","url":"http://61.151.179.194/tlivecloud-cdn.ysp.cctv.cn/cctv/2000202601.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV13","url":"http://cctvalih5ca.v.myalicdn.com/live/cctv13_2/index.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV14","url":"http://61.151.179.194/tlivecloud-cdn.ysp.cctv.cn/cctv/2000204401.m3u8","icon":"icon","state":0,"timeList":"timelist"},{"id":"","type":1,"name":"CCTV15","url":"http://61.151.179.194/tlivecloud-cdn.ysp.cctv.cn/cctv/2000205001.m3u8","icon":"icon","state":0,"timeList":"timelist"}]
     */

    private String typeId;
    private int type;
    private String typeName;
    private List<ItemsBean> items;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {
        /**
         * id :
         * type : 1
         * name : CCTV1
         * url : http://cctvalih5ca.v.myalicdn.com/live/cctv1_2/index.m3u8
         * icon : icon
         * state : 0
         * timeList : timelist
         */

        private String id;
        private int type;
        private String name;
        private String url;
        private String icon;
        private int state;
        private String timeList;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getTimeList() {
            return timeList;
        }

        public void setTimeList(String timeList) {
            this.timeList = timeList;
        }
    }
}
