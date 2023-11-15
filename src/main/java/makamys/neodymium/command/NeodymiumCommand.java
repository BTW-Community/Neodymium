package makamys.neodymium.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import makamys.neodymium.config.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WrongUsageException;
import org.lwjgl.input.Mouse;

import makamys.neodymium.Compat;
import makamys.neodymium.Compat.Warning;
import makamys.neodymium.Neodymium;
import makamys.neodymium.util.ChatUtil;
import makamys.neodymium.util.ChatUtil.MessageVerbosity;
import net.minecraft.client.Minecraft;

public class NeodymiumCommand extends CommandBase {
    
    public static final EnumChatFormatting HELP_COLOR = EnumChatFormatting.BLUE;
    public static final EnumChatFormatting HELP_USAGE_COLOR = EnumChatFormatting.YELLOW;
    public static final EnumChatFormatting HELP_WARNING_COLOR = EnumChatFormatting.YELLOW;
    public static final EnumChatFormatting HELP_EMPHASIS_COLOR = EnumChatFormatting.DARK_AQUA;
    public static final EnumChatFormatting ERROR_COLOR = EnumChatFormatting.RED;
    
    private static Map<String, ISubCommand> subCommands = new HashMap<>();
    
    public static void init() {
        //ClientCommandHandler.instance.registerCommand(new NeodymiumCommand());
        if (!MinecraftServer.getIsServer()) {
            Config.instance.registerAddonCommand(new NeodymiumCommand());
            registerSubCommand("status", new StatusCommand());
            registerSubCommand("disable_advanced_opengl", new DisableAdvancedOpenGLCommand());
        }
    }
    
    public static void registerSubCommand(String key, ISubCommand command) {
        subCommands.put(key, command);   
    }
    
    @Override
    public String getCommandName() {
        return "neodymium";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }
    
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length > 0) {
            ISubCommand subCommand = subCommands.get(args[0]);
            if(subCommand != null) {
                subCommand.processCommand(sender, args);
                return;
            }
        }
        throw new WrongUsageException(getCommandName() + " <" + String.join("|", getNonSecretSubCommands()) + ">");
    }
    
    private String[] getNonSecretSubCommands() {
        return subCommands.entrySet()
                .stream()
                .filter(e -> !e.getValue().isSecret())
                .map(Map.Entry::getKey).toArray(String[]::new);
    }
    
    public static void addChatMessage(ICommandSender sender, String text) {
        sender.sendChatToPlayer(text);
    }
    
    public static void addColoredChatMessage(ICommandSender sender, String text, EnumChatFormatting color) {
        addColoredChatMessage(sender, text, color, null);
    }
    
    public static void addColoredChatMessage(ICommandSender sender, String text, EnumChatFormatting color, Consumer<String> fixup) {
        if(fixup != null) {
            fixup.accept(text);
        }
        sender.sendChatToPlayer(text);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, getNonSecretSubCommands()) : null;
    }
    
    public static class StatusCommand implements ISubCommand {

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if(Neodymium.renderer != null) {
                List<String> text = Neodymium.renderer.getDebugText(true);
                addChatMessages(sender, text);
            }
            Neodymium.Pair<List<Warning>, List<Warning>> allWarns = Neodymium.showCompatStatus(true);
            List<Warning> warns = allWarns.getLeft();
            List<Warning> criticalWarns = allWarns.getRight();
            for(Warning line : warns) {
                addColoredChatMessageWithAction(sender, "* " + line.text, HELP_WARNING_COLOR, line.chatAction);
            }
            for(Warning line : criticalWarns) {
                addColoredChatMessageWithAction(sender, "* " + line.text, ERROR_COLOR, line.chatAction);
            }
        }
        
        private void addColoredChatMessageWithAction(ICommandSender sender, String text, EnumChatFormatting color, String command) {
            /*ChatComponentText msg = new ChatComponentText(text);
            msg.getChatStyle().setColor(color);
            if(command != null) {
                msg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            }*/
            sender.sendChatToPlayer(text);
        }

        private static void addChatMessages(ICommandSender sender, Collection<String> messages) {
            for(String line : messages) {
                sender.sendChatToPlayer(line);
            }
        }
        
    }
    
    public static class DisableAdvancedOpenGLCommand implements ISubCommand {

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            Minecraft mc = Minecraft.getMinecraft();
            if(Compat.disableAdvancedOpenGL()) {
                //mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                ChatUtil.showNeoChatMessage(EnumChatFormatting.AQUA + "Disabled Advanced OpenGL.", MessageVerbosity.INFO);
                mc.renderGlobal.loadRenderers();
            } else {
                float x = Mouse.getX() / (float)Minecraft.getMinecraft().displayWidth;
                //mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("note.bass"), 0.7F + x * 0.2F));
            }
        }
        
        @Override
        public boolean isSecret() {
            return true;
        }
        
    }
    
}
