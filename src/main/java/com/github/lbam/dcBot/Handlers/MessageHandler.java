package com.github.lbam.dcBot.Handlers;

import java.awt.Color;

import com.github.lbam.dcBot.BotMain;
import com.github.lbam.dcBot.Database.DAO.DaoPreferences;
import com.github.lbam.dcBot.Runnables.SelfDestructiveMessage;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;


public class MessageHandler {
	
	static boolean permissionWarning = false;
	
	public static IMessage sendMessage(String msg, IChannel ch) {
		try {
			return ch.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}return null;
	}
	
	public static IMessage sendMessage(String title, String body, Color color, IChannel ch) {
		EmbedObject eb = new EmbedBuilder().withColor(color).withTitle(title).withDesc(body).build();
		try {
			return new MessageBuilder(BotMain.Bot).withChannel(ch).withEmbed(eb).build();
		} catch (Exception e) {
			e.printStackTrace();
		} return null;
	}
	
	
	public static IMessage sendDestructiveMessage(String title, String body, Color color, IChannel ch, int delay) {
		EmbedObject eb = new EmbedBuilder().withColor(color).withTitle(title).withDesc(body).build();
		try {
			IMessage m = new MessageBuilder(BotMain.Bot).withChannel(ch).withEmbed(eb).build();
			Thread.sleep(delay);
			m.delete();
			return m;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void sendInvalidCommand(IChannel ch) {
		String lang = BotMain.preferences.getLang(ch.getGuild().getID());
		sendMessage(BotMain.preferences.getTitle("invalidCommand", lang).getText(), BotMain.preferences.getLocal("invalidCommand", lang).getText(), Color.red, ch);
	}
	
	public static void sendIngameError(IChannel ch) {
		String lang = BotMain.preferences.getLang(ch.getGuild().getID());
		sendMessage(BotMain.preferences.getTitle("ingameError", lang).getText(), BotMain.preferences.getLocal("ingameError", lang).getText(), Color.red, ch);
	}
	
	public static void editChampionMessage(IUser player, String representation, IMessage msg) {
		EmbedObject eb = new EmbedBuilder().withColor(Color.LIGHT_GRAY).withDesc(representation).build();
		try {
			msg.edit(player.mention(), eb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendWrongAnswer(IChannel ch, IUser user, String lang) {
		threadedDesctrutiveMessage(user.getName(), BotMain.preferences.getLocal("incorrect", lang).getText(), Color.red, ch, 2000);
	}
	
	public static void sendCorrectAnswer(IChannel ch, IUser user, String lang) {
		threadedDesctrutiveMessage(user.getName(), BotMain.preferences.getLocal("correct",lang).getText(), Color.green, ch, 2000);
	}
	
	public static void threadedDesctrutiveMessage(String title, String body, Color color, IChannel ch, int delay) {
		Thread t = new Thread(new SelfDestructiveMessage(title, body, color, ch, delay));
		t.start();
	}
	
	public static void showHelpPanel(IChannel ch){
		String lang = BotMain.preferences.getLang(ch.getGuild().getID());
		String title = BotMain.preferences.getTitle("gWelcome", lang).getText();
		String text = BotMain.preferences.getLocal("gWelcome", lang).getText();
		MessageHandler.sendMessage(title, text, Color.yellow, ch);
	}
	
	public static void deleteMessage(IMessage msg) {
		try {
			msg.delete();
		} catch (MissingPermissionsException e) {
			if(permissionWarning == false) {
				MessageHandler.sendMessage("!!!!!!", BotMain.preferences.getLocal("permission", BotMain.preferences.getLang(msg.getGuild().getID())).getText(), Color.yellow, msg.getChannel());
				permissionWarning = true;
			}
		} catch (RateLimitException e) {
			e.printStackTrace();
		} catch (DiscordException e) {
			e.printStackTrace();
		}
	}
	
	
}
