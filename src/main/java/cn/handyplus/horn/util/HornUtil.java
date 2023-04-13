package cn.handyplus.horn.util;

import cn.handyplus.horn.hook.PlaceholderApiUtil;
import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.BossBarUtil;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

/**
 * 消息工具
 *
 * @author handy
 */
public class HornUtil {

    /**
     * 发送消息
     *
     * @param player 玩家
     * @param type   类型
     * @param msg    消息
     */
    public static void sendMsg(Player player, String type, String msg) {
        if (StrUtil.isEmpty(type) || StrUtil.isEmpty(msg)) {
            MessageApi.sendConsoleMessage("消息错误:入参错误,type:" + type + ",msg:" + msg);
            return;
        }
        String rgb = ConfigUtil.CONFIG.getString("lb." + type + ".rgb");
        String name = ConfigUtil.CONFIG.getString("lb." + type + ".name");
        boolean message = ConfigUtil.CONFIG.getBoolean("lb." + type + ".message");
        boolean actionbar = ConfigUtil.CONFIG.getBoolean("lb." + type + ".actionbar.enable");
        boolean boss = ConfigUtil.CONFIG.getBoolean("lb." + type + ".boss.enable");
        boolean title = ConfigUtil.CONFIG.getBoolean("lb." + type + ".title");

        // 加载rgb颜色
        String megRgb = BaseUtil.replaceChatColor(rgb + msg);
        // 解析变量
        megRgb = PlaceholderApiUtil.set(player, megRgb);
        if (message) {
            MessageApi.sendAllMessage(megRgb);
        }
        if (actionbar) {
            String actionbarRgb = ConfigUtil.CONFIG.getString("lb." + type + ".actionbar.rgb");
            String actionbarRgbMsg = BaseUtil.replaceChatColor(actionbarRgb + msg);
            actionbarRgbMsg = PlaceholderApiUtil.set(player, actionbarRgbMsg);
            MessageApi.sendAllActionbar(actionbarRgbMsg);
        }
        if (title) {
            name = PlaceholderApiUtil.set(player, name);
            MessageApi.sendAllTitle(name, megRgb);
        }
        if (boss) {
            KeyedBossBar bossBar = BossBarUtil.createBossBar(ConfigUtil.CONFIG, "lb.boss", megRgb);
            BossBarUtil.addAllPlayer(bossBar);
            int time = ConfigUtil.CONFIG.getInt("lb." + type + ".boss.time", 3);
            BossBarUtil.removeBossBar(bossBar.getKey(), time);
        }
    }

}