package cic.cs.unb.ca.weka;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.clusterers.XMeans;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Enumeration;

/**
 * Created by yzhang29 on 02/01/18.
 */
public class WekaXMeans {
    protected static final Logger logger = LoggerFactory.getLogger(WekaXMeans.class);

    private XMeans xmeans;
    private XMeans dimenReduceXMeans;
    private Instances orgDataSet;
    private Instances dataSetWithoutStr;
    private Instances dimenReduceDataSet;
    private SummaryStatistics[] summaryStatistics;


    public WekaXMeans(Instances instances) {

        if (instances == null) {
            throw new IllegalArgumentException("instances cannot be null");
        }

        xmeans = new XMeans();

        orgDataSet = new Instances(instances);
        //logger.info("orgDataSet summary-> {}",orgDataSet.toSummaryString());
        //logger.info("orgDataSet Num: {}", orgDataSet.numAttributes());
        dataSetWithoutStr = new Instances(instances);
        dataSetWithoutStr.deleteAttributeType(Attribute.NOMINAL);
        //logger.info("dataSetWithoutStr summary-> {}",dataSetWithoutStr.toSummaryString());
        //logger.info("dataSetWithoutStr Num: {}", dataSetWithoutStr.numAttributes());

        summaryStatistics = new SummaryStatistics[dataSetWithoutStr.numAttributes()];

        dimenReduceXMeans = new XMeans();

    }

    public void build(){
        buildRaw();
        buildDimenReduction();
    }

    public void buildRaw(){

        try {
            xmeans.setSeed(10);
            xmeans.setMaxNumClusters(6);
            xmeans.setMinNumClusters(3);
            xmeans.buildClusterer(dataSetWithoutStr);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    public void buildDimenReduction() {
        try {
            dimenReduceDataSet = WekaFactory.dimReduce(dataSetWithoutStr, WekaFactory.DIMENREDUCE_TSNE);
            //dimenReduceDataSet = WekaUtils.dimReduce(dataSetWithoutStr, WekaUtils.DIMENREDUCE_WEKA_PCA);

            dimenReduceXMeans.setSeed(10);
            dimenReduceXMeans.setMaxNumClusters(6);
            dimenReduceXMeans.setMinNumClusters(3);
            dimenReduceXMeans.buildClusterer(dimenReduceDataSet);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }

    public XMeans getDRXmeans() {
        return dimenReduceXMeans;
    }

    public Instances getDRDataset() {
        return dimenReduceDataSet;
    }

    public int getAttrIndex(Attribute attribute) {
        int index = -1;

        for(int i = 0; i< dataSetWithoutStr.numAttributes(); i++) {
            Attribute attr = dataSetWithoutStr.attribute(i);

            if(attr.name().equals(attribute.name())) {
                index = i;
                if(summaryStatistics[i]==null) {
                    summaryStatistics[i] = new SummaryStatistics();
                }
                break;
            }
        }
        return index;
    }

    public double getMean(Attribute attribute) {
        int index  = getAttrIndex(attribute);

        if (index < 0) {
            logger.info("not found {} in the data set!", attribute.name());
            return 0;
        }

        if(summaryStatistics[index]==null) {
            summaryStatistics[index] = new SummaryStatistics();
        }
        double[] values = dataSetWithoutStr.attributeToDoubleArray(index);

        if(summaryStatistics.length != values.length) {
            for(double value:values) {
                summaryStatistics[index].addValue(value);
            }
        }
        return summaryStatistics[index].getMean();
    }

    private int getAttrIndex(Instances dataSet, Attribute attribute) {
        int index = -1;
        for(int i = 0; i< dataSet.numAttributes(); i++) {
            Attribute attr = dataSet.attribute(i);

            if(attr.name().equals(attribute.name())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public Multimap<String, Instance> getMultiMap(Attribute attribute) {

        if (attribute == null) {
            throw new IllegalArgumentException("attribute should not be null!");
        }

        Instances instances;
        if (attribute.isNumeric()) {
            instances = dataSetWithoutStr;
        } else {
            instances = orgDataSet;
        }

        int index  = getAttrIndex(instances,attribute);
        if (index < 0) {
            logger.info("not found {} in the data set!", attribute.name());
            return null;
        }
        Multimap<String, Instance> attrMap = ArrayListMultimap.create();

        Enumeration enumInst = instances.enumerateInstances();
        while (enumInst.hasMoreElements()) {
            Instance inst = (Instance) enumInst.nextElement();

            String key;
            if (attribute.isNumeric()) {
                key = String.valueOf(inst.value(index));
            } else {
                key = inst.stringValue(index);
            }
            attrMap.put(key, inst);
        }

        return attrMap;
    }

}
