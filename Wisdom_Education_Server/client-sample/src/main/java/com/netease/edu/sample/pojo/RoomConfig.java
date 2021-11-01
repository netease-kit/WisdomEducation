package com.netease.edu.sample.pojo;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by weiliang02
 */
public class RoomConfig {
    private Long configId;
    private String sceneType;
    private Map<String, RoleConfig> roleConfigs;
    /**
     * room-properties|states-{key}-AttributePermission
     * member-properties|streams-{key}-AttributePermission
     */
    private Map<String, Map<String, Map<String, AttributePermission>>> permissions;
    private Map<String, Map<String, Map<String, Map<String, Object>>>> initAttrs;
    private List<PropertyChangeTrigger> propertyChangeTriggers;
    protected RoomResourceConfig resource;

    public RoomConfig(RoomConfig roomConfig){
        this.configId = roomConfig.configId;
        this.sceneType = roomConfig.sceneType;
        this.roleConfigs = roomConfig.roleConfigs;
        this.permissions = roomConfig.permissions;
        this.initAttrs = roomConfig.initAttrs;
        this.propertyChangeTriggers = roomConfig.propertyChangeTriggers;
        //TODO 这里只做了resource的merge 其他的还要处理 如果有需要
        this.resource = new RoomResourceConfig(roomConfig.resource);
    }
    public RoomConfig() {
    }

    public RoomConfig(Long configId, RoomResourceConfig resourceConfig) {
        this.configId = configId;
        this.resource = resourceConfig;
    }

    public Map<String, RoleConfig> getRoleConfigs() {
        return roleConfigs;
    }

    public AttributePermission getRoomStatesKeyPermission(String key){
        return Optional.ofNullable(permissions).map(permissions->permissions.get("room")).map(room-> room.get("states")).map(props->props.get(key)).orElse(new AttributePermission());
    }

    public AttributePermission getRoomPropsKeyPermission(String key){
        return Optional.ofNullable(permissions).map(permissions->permissions.get("room")).map(room-> room.get("properties")).map(props->props.get(key)).orElse(new AttributePermission());
    }
    public AttributePermission getMemberPropsKeyPermission(String key){
        return Optional.ofNullable(permissions).map(permissions->permissions.get("member")).map(room-> room.get("properties")).map(props->props.get(key)).orElse(new AttributePermission());
    }
    public AttributePermission getMemberStreamTypePermission(String type){
        return Optional.ofNullable(permissions).map(permissions->permissions.get("member")).map(room-> room.get("streams")).map(props->props.get(type)).orElse(new AttributePermission());
    }

    public Map<String, AttributePermission> getMemberStreamsPermissionMap(){
        return Optional.ofNullable(permissions).map(permissions->permissions.get("member")).map(room-> room.get("streams")).orElse(Maps.newHashMap());
    }
    public Map<String, AttributePermission> getMemberPropertiesPermissionMap(){
        return Optional.ofNullable(permissions).map(permissions->permissions.get("member")).map(room-> room.get("properties")).orElse(Maps.newHashMap());
    }

    public Map<String, Map<String, Object>> getInitRoomStates(){
        return Optional.ofNullable(initAttrs).map(permissions->permissions.get("room")).map(room-> room.get("states")).map(Maps::newHashMap).orElse(Maps.newHashMap());
    }

    public Map<String, Map<String, Object>> getInitRoomProperties(){
        return Optional.ofNullable(initAttrs).map(permissions->permissions.get("room")).map(room-> room.get("properties")).map(Maps::newHashMap).orElse(Maps.newHashMap());
    }

    public Map<String, Map<String, Object>> getInitMemberProperties(){
        return Optional.ofNullable(initAttrs).map(permissions->permissions.get("member")).map(room-> room.get("properties")).map(Maps::newHashMap).orElse(Maps.newHashMap());
    }

    public Map<String, Map<String, Object>> getInitMemberStreams(){
        return Optional.ofNullable(initAttrs).map(permissions->permissions.get("member")).map(room-> room.get("streams")).map(Maps::newHashMap).orElse(Maps.newHashMap());
    }

    public RoomResourceConfig getRoomResourceConfig() {
        return resource;
    }

    public void setRoomResourceConfig(RoomResourceConfig resource) {
        this.resource = resource;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public RoleConfig roleConfig(String role){
        return this.getRoleConfigs().get(role);
    }

    public boolean rtcEnable(){
        return Optional.ofNullable(resource).map(RoomResourceConfig::rtc).orElse(true);
    }
    public boolean chatRoom(){
        return Optional.ofNullable(resource).map(RoomResourceConfig::chatroom).orElse(true);
    }
    public boolean live(){
        return Optional.ofNullable(resource).map(RoomResourceConfig::live).orElse(false);
    }
    public boolean whiteboard(){
        return Optional.ofNullable(resource).map(RoomResourceConfig::whiteboard).orElse(true);
    }
    static class PropertyChangeTrigger{
        private String key;
        private Boolean targetReverse;
        private Set<String> roles;
        private Map<String, Object> target;
        private Map<String, Object> current;

        private TriggerOperation operation;

        public TriggerOperation getOperation() {
            return operation;
        }

        public void setOperation(TriggerOperation operation) {
            this.operation = operation;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Boolean getTargetReverse() {
            return targetReverse;
        }

        public void setTargetReverse(Boolean targetReverse) {
            this.targetReverse = targetReverse;
        }

        public Map<String, Object> getTarget() {
            return target;
        }

        public void setTarget(Map<String, Object> target) {
            this.target = target;
        }

        public Map<String, Object> getCurrent() {
            return current;
        }

        public void setCurrent(Map<String, Object> current) {
            this.current = current;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }
    }
    static class TriggerOperation{
        private Set<String> deletingStreamTypes;
        private Set<String> deletingPropertyKeys;

        public Set<String> getDeletingStreamTypes() {
            return deletingStreamTypes;
        }

        public void setDeletingStreamTypes(Set<String> deletingStreamTypes) {
            this.deletingStreamTypes = deletingStreamTypes;
        }

        public Set<String> getDeletingPropertyKeys() {
            return deletingPropertyKeys;
        }

        public void setDeletingPropertyKeys(Set<String> deletingPropertyKeys) {
            this.deletingPropertyKeys = deletingPropertyKeys;
        }
    }
}
