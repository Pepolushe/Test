package com.github.lbam.dcBot.Handlers;


import java.util.ArrayList;

import com.github.lbam.dcBot.BotMain;
import com.github.lbam.dcBot.Commands.Callback;
import com.github.lbam.dcBot.Commands.GameReceiver;
import com.github.lbam.dcBot.Database.DAO.DaoPreferences;
import com.github.lbam.dcBot.Interfaces.Command;


import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class EventHandler {
	
	ArrayList<IChannel> channels = new ArrayList<IChannel>();
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event){
		System.out.println("Entrou");
		for(IChannel ch : channels) {
			MessageHandler.showHelpPanel(ch);
		}
	}
	
	@EventSubscriber
	public void onGuildCreateEvent(GuildCreateEvent event) {
		IGuild server = event.getGuild();
		String serverRegion = server.getRegion().getName();
		
		if(!DaoPreferences.existeRegistro(server.getID())){
			if(serverRegion.equals("Brazil")){
				DaoPreferences.createPreferences(server.getID(), "br");
			}else{
				DaoPreferences.createPreferences(server.getID(), "us");
				try {
					BotMain.Bot.changeUsername("Who is that champion?");
				} catch (Exception e){
					System.out.println("Não pude mudar o nick :(");
				}
			}
		}
		System.out.println("??g");
		for(IChannel ch : server.getChannels()) {
			channels.add(ch);
		}
		
	}
	@EventSubscriber
	public void onMessageEvent(MessageReceivedEvent event) throws DiscordException, RateLimitException, MissingPermissionsException{
		IMessage message = event.getMessage();
		String[] args = message.getContent().split(" ");
		
		System.out.println("??");
		if(message.getAuthor().isBot())
			return;

		if(args[0].equals(":dc") && args.length > 1) {
			System.out.println("??x");
			Command cmd = new Callback(new GameReceiver(message.getAuthor(),message.getChannel()), args[1], message.getChannel());
			cmd.execute();
			System.out.println("??c");
		} 
		else
			return;
		
	}
}
