package org.hy.common.xml.plugins;

import org.hy.common.Help;
import org.hy.common.TablePartitionRID;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;





/**
 * 单位转换。如，毫米->米； 摄氏度->华氏度 。
 * 
 *   1. 默认转换后，保留最高精度。不四舍五入
 *   2. 支持中文单位名称的转换
 *   3. 支持主要参数、次要参数两个参数的转换
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-02-29
 * @version     v1.0
 */
@Xjava(XType.XML) 
public class UnitConvert
{
    private static boolean                           $IsInit = false;
    
    /**
     * 分区字段为：原单位
     * 分区主键为：转换后的目标单位
     * 分区记录为：转换表达式
     */
    private static TablePartitionRID<String ,String> $UnitConvert;
    
    
    
    /**
     * 单位转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-29
     * @version     v1.0
     *
     * @param i_SourceUnit  原单位
     * @param i_TargetUnit  转换后的目标单位
     * @param i_Value       主要参数
     * @return
     */
    public static Number convert(String i_SourceUnit ,String i_TargetUnit ,String i_Value)
    {
        return convert(i_SourceUnit ,i_TargetUnit ,null ,Double.valueOf(i_Value));
    }
    
    
    
    /**
     * 单位转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-29
     * @version     v1.0
     *
     * @param i_SourceUnit  原单位
     * @param i_TargetUnit  转换后的目标单位
     * @param i_Digit       保留几位小数。为 null 表示保留最高精度。即，不四舍五入
     * @param i_Value       主要参数
     * @return
     */
    public static Number convert(String i_SourceUnit ,String i_TargetUnit ,Integer i_Digit ,String i_Value)
    {
        return convert(i_SourceUnit ,i_TargetUnit ,i_Digit ,Double.valueOf(i_Value));
    }
    
    
    
    /**
     * 单位转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-04-06
     * @version     v1.0
     *
     * @param i_SourceUnit  原单位
     * @param i_TargetUnit  转换后的目标单位
     * @param i_Value       主要参数
     * @param i_Others      次要参数组（可为空）
     * @return
     */
    public static Number convert(String i_SourceUnit ,String i_TargetUnit ,String i_Value ,String ... i_Others)
    {
        return convert(i_SourceUnit ,i_TargetUnit ,null ,i_Value ,i_Others);
    }
    
    
    
    /**
     * 单位转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-04-06
     * @version     v1.0
     *
     * @param i_SourceUnit  原单位
     * @param i_TargetUnit  转换后的目标单位
     * @param i_Digit       保留几位小数。为 null 表示保留最高精度。即，不四舍五入
     * @param i_Value       主要参数
     * @param i_Others      次要参数组（可为空）
     * @return
     */
    public static Number convert(String i_SourceUnit ,String i_TargetUnit ,Integer i_Digit ,String i_Value ,String ... i_Others)
    {
        if ( Help.isNull(i_Value) )
        {
            return null;
        }
        
        if ( Help.isNull(i_Others) )
        {
            return convert(i_SourceUnit ,i_TargetUnit ,i_Digit ,Double.valueOf(i_Value));
        }
        else
        {
            Double [] v_Others = new Double[i_Others.length];
            for (int i=0; i<i_Others.length; i++)
            {
                if ( !Help.isNull(i_Others[i]) )
                {
                    v_Others[i] = Double.valueOf(i_Others[i]);
                }
            }
            return convert(i_SourceUnit ,i_TargetUnit ,i_Digit ,Double.valueOf(i_Value) ,v_Others);
        }
    }
    
    
    
    /**
     * 单位转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-29
     * @version     v1.0
     *
     * @param i_SourceUnit  原单位
     * @param i_TargetUnit  转换后的目标单位
     * @param i_Value       主要参数
     * @return
     */
    public static <N extends Number> Number convert(String i_SourceUnit ,String i_TargetUnit ,N i_Value)
    {
        return convert(i_SourceUnit ,i_TargetUnit ,null ,i_Value ,new Number[]{});
    }
    
    
    
    /**
     * 单位转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-29
     * @version     v1.0
     *
     * @param i_SourceUnit  原单位
     * @param i_TargetUnit  转换后的目标单位
     * @param i_Digit       保留几位小数。为 null 表示保留最高精度。即，不四舍五入
     * @param i_Value       主要参数
     * @return
     */
    public static <N extends Number> Number convert(String i_SourceUnit ,String i_TargetUnit ,Integer i_Digit ,N i_Value)
    {
        return convert(i_SourceUnit ,i_TargetUnit ,i_Digit ,i_Value ,new Number[]{});
    }
    
    
    
    /**
     * 单位转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-04-06
     * @version     v1.0
     *
     * @param i_SourceUnit  原单位
     * @param i_TargetUnit  转换后的目标单位
     * @param i_Digit       保留几位小数。为 null 表示保留最高精度。即，不四舍五入
     * @param i_Value       主要参数
     * @param i_Others      次要参数组（可为空）
     * @return
     */
    /*
     * 不能有此方法，因为当i_Value为int类型时，会出现冲突
    public static <N extends Number> Number convert(String i_SourceUnit ,String i_TargetUnit ,N i_Value ,N ... i_Others)
    {
        return convert(i_SourceUnit ,i_TargetUnit ,null ,i_Value ,i_Others);
    }
    */
    
    
    
    /**
     * 单位转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-29
     * @version     v1.0
     *
     * @param i_SourceUnit  原单位
     * @param i_TargetUnit  转换后的目标单位
     * @param i_Digit       保留几位小数。为 null 表示保留最高精度。即，不四舍五入
     * @param i_Value       主要参数
     * @param i_Others      次要参数组（可为空）
     * @return
     */
    public static <N extends Number> Number convert(String i_SourceUnit ,String i_TargetUnit ,Integer i_Digit ,N i_Value ,N ... i_Others)
    {
        if ( Help.isNull(i_SourceUnit) 
          || Help.isNull(i_TargetUnit) 
          || i_SourceUnit.equals(i_TargetUnit)
          || null == i_Value )
        {
            return i_Value;
        }
        
        init();
        
        String v_Expression = $UnitConvert.getRow(i_SourceUnit ,i_TargetUnit);
        
        if ( Help.isNull(v_Expression) )
        {
            return null;
        }
        
        FelEngine  v_Fel        = new FelEngineImpl();
        FelContext v_FelContext = v_Fel.getContext();
        v_FelContext.set("UCE" ,i_Value);
        if ( !Help.isNull(i_Others) )
        {
            for (int i=0; i<i_Others.length; i++)
            {
                if ( null != i_Others[i] )
                {
                    v_FelContext.set("P0" + (i+1) ,i_Others[i]);
                }
            }
        }
        
        if ( null == i_Digit )
        {
            return (Number)v_Fel.eval(v_Expression);
        }
        else
        {
            return Help.round((Number)v_Fel.eval(v_Expression) ,i_Digit);
        }
    }
    
    
    
    /**
     * 允许外界扩展单位转换
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-29
     * @version     v1.0
     *
     * @param i_SourceUnit  原单位
     * @param i_TargetUnit  转换后的目标单位
     * @param i_Expression  表达式。主参数用UCE表示，次要参数用P01表示。
     */
    public static synchronized void addExpression(String i_SourceUnit ,String i_TargetUnit ,String i_Expression)
    {
        if ( Help.isNull(i_SourceUnit) || Help.isNull(i_TargetUnit) || Help.isNull(i_Expression) )
        {
            return;
        }
        
        init();
        $UnitConvert.putRow(i_SourceUnit ,i_TargetUnit ,i_Expression);
    }
    
    
    
    /**
     * 加载配置信息，并初始化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-29
     * @version     v1.0
     *
     */
    @SuppressWarnings("unchecked")
    private static synchronized void init()
    {
        if ( !$IsInit )
        {
            $IsInit = true;
            try
            {
                XJava.parserAnnotation(UnitConvert.class.getName());
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
            
            $UnitConvert = (TablePartitionRID<String ,String>)XJava.getObject("UnitConvertExpressions");
        }
    }
    
}
