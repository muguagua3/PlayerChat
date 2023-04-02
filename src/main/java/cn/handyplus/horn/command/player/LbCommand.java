package cn.handyplus.horn.command.player;

import cn.handyplus.horn.enter.HornPlayerEnter;
import cn.handyplus.horn.param.LbMessage;
import cn.handyplus.horn.service.HornPlayerService;
import cn.handyplus.horn.util.ConfigUtil;
import cn.handyplus.horn.util.HornUtil;
import cn.handyplus.lib.annotation.HandyCommand;
import cn.handyplus.lib.api.MessageApi;
import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.core.JsonUtil;
import cn.handyplus.lib.util.AssertUtil;
import cn.handyplus.lib.util.BaseUtil;
import cn.handyplus.lib.util.BcUtil;
import cn.handyplus.lib.util.HandyPermissionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 发送消息
 *
 * @author handy
 */
@HandyCommand(name = "lb", permission = "riceHorn.lb", PERMISSION_DEFAULT = PermissionDefault.TRUE)
public class LbCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 参数是否正常
        if (args.length < 2) {
            MessageApi.sendMessage(sender, "参数错误 /lb [喇叭类型] [消息内容]");
            return true;
        }
        // 是否为玩家
        if (BaseUtil.isNotPlayer(sender)) {
            MessageApi.sendMessage(sender, "只能玩家执行");
            return true;
        }
        Player player = AssertUtil.notPlayer(sender, "只能玩家执行");
        // 获取类型
        String type = args[0];
        List<String> serverList = ConfigUtil.CONFIG.getStringList("lb." + type + ".server");
        if (CollUtil.isEmpty(serverList)) {
            MessageApi.sendMessage(sender, "配置错误");
            return true;
        }
        HornPlayerEnter hornPlayerEnter = HornPlayerService.getInstance().findByUidAndType(player.getUniqueId().toString(), type);
        if (hornPlayerEnter == null) {
            MessageApi.sendMessage(player, "&a你没有可使用的喇叭");
            return true;
        }
        if (hornPlayerEnter.getNumber() < 1) {
            MessageApi.sendMessage(player, "&a你的喇叭数量不足");
            return true;
        }
        // 进行扣除
        HornPlayerService.getInstance().subtractNumber(hornPlayerEnter.getId(), 1);
        // 获取消息
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        LbMessage lbMessage = new LbMessage();
        lbMessage.setType(type);
        lbMessage.setMessage(message.toString());
        BcUtil.sendForward(player, JsonUtil.toJson(lbMessage));
        // 发送消息
        HornUtil.sendMsg(player, type, message.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = null;
        if (args.length == 1) {
            Map<String, String> lb = HandyPermissionUtil.getStringMapChild(ConfigUtil.CONFIG, "lb");
            commands = new ArrayList<>(lb.keySet());
        }
        if (commands == null) {
            return new ArrayList<>();
        }
        StringUtil.copyPartialMatches(args[args.length - 1].toLowerCase(), commands, completions);
        Collections.sort(completions);
        return completions;
    }

}