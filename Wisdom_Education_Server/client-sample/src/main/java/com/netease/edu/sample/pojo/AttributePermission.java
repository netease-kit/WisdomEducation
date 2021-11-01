package com.netease.edu.sample.pojo;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * Created by weiliang
 */
public class AttributePermission {
    private JsonElement roles;
    private Map<String, Map<String, Object>> memberGrant;
    private Boolean exclusive;
    private Concurrency concurrency;

    private Set<String> parseRoleExpression(String expression){
        Set<String> roleExpressions = Sets.newHashSet();
        if(expression == null){
            return roleExpressions;
        }
        //host#put|delete
        //broadcaster.self#delete
        //host.self|broadcaster#put|delete
        String [] splits = expression.split("#");
        boolean put = false;
        boolean delete = false;
        if(splits.length == 2){
            if(StringUtils.isNoneEmpty(splits[1])){
                for (String s : splits[1].split("\\|")) {
                    if(s.trim().equalsIgnoreCase("put")){
                        put = true;
                    }
                    if(s.trim().equalsIgnoreCase("delete")){
                        delete = true;
                    }
                }
            }
        }else if (splits.length != 1){
            return null;
        }
        expression = splits[0];
        splits = expression.split("\\.");
        if(splits.length == 1){
            String operatorRole = splits[0];
            if (put) {
                roleExpressions.add(operatorRole + "#put");
            }
            if(delete){
                roleExpressions.add(operatorRole + "#delete");
            }
            if(!put && !delete){
                roleExpressions.add(operatorRole);
            }
        }else if( splits.length == 2){
            String operatorRole = splits[0];
            expression = splits[1];
            splits = expression.split("\\|");
            for (String destinationRole : splits) {
                if(put){
                    roleExpressions.add(operatorRole + "." + destinationRole + "#put");
                }
                if(delete){
                    roleExpressions.add(operatorRole + "." + destinationRole + "#delete");
                }
                if(!put && !delete){
                    roleExpressions.add(operatorRole + "." + destinationRole);
                }
            }
        }else{
            return null;
        }
        return roleExpressions;
    }

    public Map<String, Map<String, Object>> getMemberGrant() {
        return memberGrant;
    }

    public boolean exclusive() {
        return BooleanUtils.isTrue(exclusive);
    }


    public Concurrency getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Concurrency concurrency) {
        this.concurrency = concurrency;
    }

    public static class Concurrency{
        private Integer limit;
        private Map<String, Object> valueMap;

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public Map<String, Object> getValueMap() {
            return valueMap;
        }

        public void setValueMap(Map<String, Object> valueMap) {
            this.valueMap = valueMap;
        }
    }


}
