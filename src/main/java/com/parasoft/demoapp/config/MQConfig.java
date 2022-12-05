package com.parasoft.demoapp.config;

import com.parasoft.demoapp.model.global.preferences.MqType;

public class MQConfig {
    /**
     * This static variable is a snapshot of the MQ_TYPE value in DB(global preferences table)
     * <br/>
     * The default value is MqType.ACTIVE_MQ, need to change the value when the MQ_TYPE is changed manually(through API) or when PDA starts up.
     */
    public static volatile MqType CURRENT_MQ_TYPE = MqType.ACTIVE_MQ;
}
