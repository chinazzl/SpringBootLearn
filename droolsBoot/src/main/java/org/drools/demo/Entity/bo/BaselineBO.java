package org.drools.demo.Entity.bo;

import java.util.List;

/**
 * @author Julyan
 * @version V1.0
 * @Date: 2023/2/15 16:31
 * @Description: {
 * "xAxis": [
 * "xxx",
 * "xxx"
 * ],
 * "series": {
 * "upTline": [],
 * "downTline": [],
 * "upBaseline": [],
 * "downBaseline": []
 * }
 * }
 */
public class BaselineBO {
    private List<String> xAxis;

    private Series series;


    public class Series {
        private List<Double> upTline;
        private List<Double> downTline;
        private List<Double> upBaseline;
        private List<Double> downBaseline;

        public List<Double> getUpTline() {
            return upTline;
        }

        public void setUpTline(List<Double> upTline) {
            this.upTline = upTline;
        }

        public List<Double> getDownTline() {
            return downTline;
        }

        public void setDownTline(List<Double> downTline) {
            this.downTline = downTline;
        }

        public List<Double> getUpBaseline() {
            return upBaseline;
        }

        public void setUpBaseline(List<Double> upBaseline) {
            this.upBaseline = upBaseline;
        }

        public List<Double> getDownBaseline() {
            return downBaseline;
        }

        public void setDownBaseline(List<Double> downBaseline) {
            this.downBaseline = downBaseline;
        }
    }

    public List<String> getxAxis() {
        return xAxis;
    }

    public void setxAxis(List<String> xAxis) {
        this.xAxis = xAxis;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }
}
