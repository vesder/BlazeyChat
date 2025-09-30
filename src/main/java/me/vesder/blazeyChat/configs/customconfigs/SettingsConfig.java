package me.vesder.blazeyChat.configs.customconfigs;

import lombok.Getter;
import me.vesder.blazeyChat.configs.ConfigUtils;
import me.vesder.blazeyChat.configs.CustomConfig;

import java.util.List;

public class SettingsConfig extends CustomConfig {

    // Defaults
    @Getter
    private String defaultPrefix;
    // Chat
    @Getter
    private boolean chatPerWorld;
    @Getter
    private String chatNoPermError;
    // Reload
    @Getter
    private List<String> reloadAllActions;
    @Getter
    private List<String> reloadSpecificActions;
    // Shout
    @Getter
    private String shoutFlag;
    @Getter
    private String shoutFormat;
    @Getter
    private List<String> shoutSenderActions;
    @Getter
    private List<String> shoutReceiverActions;
    @Getter
    private List<String> shoutEnableActions;
    @Getter
    private List<String> shoutDisableActions;
    // ChatSpy
    @Getter
    private String chatspyFormat;
    @Getter
    private boolean chatspyOnJoin;
    @Getter
    private List<String> chatspyEnableActions;
    @Getter
    private List<String> chatspyDisableActions;
    // MSG
    @Getter
    private List<String> pvMessagesSenderActions;
    @Getter
    private List<String> pvMessagesReceiverActions;
    @Getter
    private List<String> replyNotFoundActions;
    @Getter
    private List<String> replySetActions;
    @Getter
    private String pvMessagesSelfMsgError;
    @Getter
    private String pvMessagesNotFoundError;
    @Getter
    private String pvMessagesNoMsgError;
    // IGNORE
    @Getter
    private List<String> ignoreAddActions;
    @Getter
    private List<String> ignoreRemoveActions;
    @Getter
    private List<String> ignoreAllEnableActions;
    @Getter
    private List<String> ignoreAllDisableActions;
    @Getter
    private List<String> ignoreClearActions;
    @Getter
    private String ignoreListStored;
    @Getter
    private String ignoreListEmpty;
    @Getter
    private String ignoreSelfIgnoreError;
    @Getter
    private String ignoreNotFoundError;


    @Override
    public String getName() {
        return "settings.yml";
    }

    @Override
    public void loadValues() {
        // Defaults Section
        defaultPrefix = ConfigUtils.getStringConfig(getName(), "defaults.prefix");
        // Chat Section
        chatPerWorld = ConfigUtils.getBooleanConfig(getName(), "chat.per-world");
        chatNoPermError = ConfigUtils.getStringConfig(getName(), "chat.errors.no-perm");
        // Reload Section
        reloadAllActions = ConfigUtils.getStringListConfig(getName(), "reload.all.actions");
        reloadSpecificActions = ConfigUtils.getStringListConfig(getName(), "reload.specific.actions");
        // Shout Section
        shoutFlag = ConfigUtils.getStringConfig(getName(), "shout.flag");
        shoutFormat = ConfigUtils.getStringConfig(getName(), "shout.format");
        shoutSenderActions = ConfigUtils.getStringListConfig(getName(), "shout.sender.actions");
        shoutReceiverActions = ConfigUtils.getStringListConfig(getName(), "shout.receiver.actions");
        shoutEnableActions = ConfigUtils.getStringListConfig(getName(), "shout.enable.actions");
        shoutDisableActions = ConfigUtils.getStringListConfig(getName(), "shout.disable.actions");
        // ChatSpy Section
        chatspyOnJoin = ConfigUtils.getBooleanConfig(getName(), "chatspy.on-join");
        chatspyFormat = ConfigUtils.getStringConfig(getName(), "chatspy.format");
        chatspyEnableActions = ConfigUtils.getStringListConfig(getName(), "chatspy.enable.actions");
        chatspyDisableActions = ConfigUtils.getStringListConfig(getName(), "chatspy.disable.actions");
        // MSG Section
        pvMessagesSenderActions = ConfigUtils.getStringListConfig(getName(), "pv-messages.sender.actions");
        pvMessagesReceiverActions = ConfigUtils.getStringListConfig(getName(), "pv-messages.receiver.actions");
        replySetActions = ConfigUtils.getStringListConfig(getName(), "pv-messages.reply.set.actions");
        replyNotFoundActions = ConfigUtils.getStringListConfig(getName(), "pv-messages.reply.not-found.actions");
        pvMessagesSelfMsgError = ConfigUtils.getStringConfig(getName(), "pv-messages.errors.self-msg");
        pvMessagesNotFoundError = ConfigUtils.getStringConfig(getName(), "pv-messages.errors.not-found");
        pvMessagesNoMsgError = ConfigUtils.getStringConfig(getName(), "pv-messages.errors.no-msg");
        // Ignore Section
        ignoreAddActions = ConfigUtils.getStringListConfig(getName(), "ignore.add.actions");
        ignoreRemoveActions = ConfigUtils.getStringListConfig(getName(), "ignore.remove.actions");
        ignoreAllEnableActions = ConfigUtils.getStringListConfig(getName(), "ignore.all.enable.actions");
        ignoreAllDisableActions = ConfigUtils.getStringListConfig(getName(), "ignore.all.disable.actions");
        ignoreClearActions = ConfigUtils.getStringListConfig(getName(), "ignore.clear.actions");
        ignoreListStored = ConfigUtils.getStringConfig(getName(), "ignore.list.stored");
        ignoreListEmpty = ConfigUtils.getStringConfig(getName(), "ignore.list.empty");
        ignoreSelfIgnoreError = ConfigUtils.getStringConfig(getName(), "ignore.errors.self-ignore");
        ignoreNotFoundError = ConfigUtils.getStringConfig(getName(), "ignore.errors.not-found");

    }
}
