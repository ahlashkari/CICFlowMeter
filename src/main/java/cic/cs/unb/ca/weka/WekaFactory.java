package cic.cs.unb.ca.weka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PrincipalComponents;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by yzhang29 on 02/01/18.
 */
public class WekaFactory {

    protected static final Logger logger = LoggerFactory.getLogger(WekaFactory.class);

    private static WekaFactory instanceFactory = new WekaFactory();

    //private Instances mEmptyFlowInstances;


    public static final String DIMENREDUCE_TSNE = "t-sne";
    public static final String DIMENREDUCE_WEKA_PCA = "weka-pca";

    public static final Attribute DRATTRX = new Attribute("X");
    public static final Attribute DRATTRY = new Attribute("Y");


    public static WekaFactory getFactory() {
        return instanceFactory;
    }

    public WekaFactory init() {

        /*List<FlowFeature> featureList = FlowFeature.getFeatureList();
        FastVector fastVector = new FastVector();
        for (FlowFeature feature : featureList) {
            Attribute attribute = feature2attr(feature.getName(),feature.isNumeric());
            fastVector.addElement(attribute);
        }
        mEmptyFlowInstances = new Instances("cic_flow_feature",fastVector,0);
        logger.debug("{}",mEmptyFlowInstances.toSummaryString());*/

        return instanceFactory;
    }

    /*public Instances createEmptyFlowInstances() {
        return new Instances(mEmptyFlowInstances,0);
    }

    public int numAttributesOfFlowInstances() {
        return mEmptyFlowInstances.numAttributes();
    }*/

    public static Instances loadURLCsv(File file) {
        logger.debug("loadURLCsv {}",file.getPath());
        Instances instances=null;

        CSVLoader loader = new CSVLoader();

        try {
            loader.setSource(file);
            instances = loader.getDataSet();

            //logger.info("loadURLCsv org->> {}",instances.toSummaryString());
            instances.deleteAttributeType(Attribute.NOMINAL);

            Enumeration<Attribute> enuAttr = instances.enumerateAttributes();
            while(enuAttr.hasMoreElements()) {
                instances.deleteWithMissing(enuAttr.nextElement());
            }
            logger.debug("loadURLCsv summary-> {}",instances.toSummaryString());

        } catch (IOException e) {
            logger.debug(e.getMessage());
        }

        return instances;
    }

    public static Instances loadFlowCsv(File file) {
        logger.debug("loadFlowCsv {}",file.getPath());
        Instances instances=null;

        CSVLoader loader = new CSVLoader();
        try {
            loader.setSource(file);
            instances = loader.getDataSet();

            Enumeration<Attribute> enuAttr = instances.enumerateAttributes();
            while(enuAttr.hasMoreElements()) {
                instances.deleteWithMissing(enuAttr.nextElement());
            }
            logger.debug("loadFlowCsv summary-> {}",instances.toSummaryString());

        }catch(IOException e) {
            logger.debug(e.getMessage());
        }
        return instances;
    }

    private static Instances createEmptyDimReInstances(String dimReAlgorithm) {
        FastVector fv = new FastVector();
        fv.addElement(DRATTRX);
        fv.addElement(DRATTRY);
        Instances insts = new Instances(dimReAlgorithm,fv,0);
        //logger.info("{}",insts.toSummaryString());
        return insts;
    }

    private static DimenReduce getDimenReduceMethod(String arg) {

        DimenReduce dr=null;

        switch(arg) {
            case DIMENREDUCE_TSNE:
                //dr =  new DimReImplTSNE();
                break;
        }
        return dr;
    }

    public static Instances dimReduce(final Instances org, String arg) {

        Instances instances = createEmptyDimReInstances(arg);

        DimenReduce dr = getDimenReduceMethod(arg);

        if(arg==null||arg.equals(DIMENREDUCE_WEKA_PCA)||dr==null) {
            try {
                PrincipalComponents pca = new PrincipalComponents();
                pca.setMaximumAttributes(2);
                pca.setInputFormat(org);
                instances = Filter.useFilter(org, pca);
            }catch(Exception e) {
                logger.debug(e.getMessage());
            }
        }else{
            double[][] orgData = instances2doubleArray(org);

            double[][] newData = dr.dimensionReduce(orgData);

            for(int i=0;i<newData.length;i++) {
                Instance inst = new Instance(instances.numAttributes());
                inst.setValue(DRATTRX, newData[i][0]);
                inst.setValue(DRATTRY, newData[i][1]);
                instances.add(inst);
            }
        }
        logger.debug("{}",instances.toSummaryString());
        return instances;
    }

    public static double[][] instances2doubleArray(final Instances instances){

        int num = instances.numInstances();

        double[][] ret  = new double[num][];

        for(int i=0;i<num;i++) {
            Instance inst = instances.instance(i);
            ret[i] = inst.toDoubleArray();
        }
        return ret;
    }

    public static Attribute feature2attr(String featurename,boolean isNumeric){
        Attribute attr;
        if(isNumeric) {
            attr = new Attribute(featurename);
        }else {
            attr = new Attribute(featurename, (FastVector) null);
        }
        return attr;
    }

}
