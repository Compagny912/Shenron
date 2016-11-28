package org.sdd.shenron.command;

import fr.litarvan.krobot.command.ICommandCaller;
import fr.litarvan.krobot.command.message.MessageCommandCaller;
import fr.litarvan.krobot.motor.Message;
import fr.litarvan.krobot.motor.discord.DiscordConversation;
import fr.litarvan.krobot.motor.discord.DiscordMessage;
import java.util.List;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.jetbrains.annotations.NotNull;
import org.sdd.shenron.Shenron;
import org.sdd.shenron.util.Emoji;

public class CommandWordReact extends ShenronCommand
{
    @NotNull
    @Override
    public String getCommand()
    {
        return "wr";
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "Add a word as reactions letters emotes on the last message";
    }

    @NotNull
    @Override
    public String getSyntax()
    {
        return "<word>";
    }

    @Override
    public boolean checkSyntax(List<String> list)
    {
        return list.size() == 1;
    }

    @Override
    public void handle(ICommandCaller cCaller, List<String> args) throws RateLimitedException
    {
        if (!(cCaller instanceof MessageCommandCaller))
        {
            return;
        }

        MessageCommandCaller caller = (MessageCommandCaller) cCaller;

        if (!(caller.getConversation() instanceof DiscordConversation))
        {
            return;
        }

        Message[] lasts = caller.getConversation().messages(2);

        // TODO: Krobot Delete
        ((DiscordMessage) lasts[0]).getMessage().deleteMessage().block();

        Emoji[] reactions = Emoji.textToEmoji(args.get(0).trim().toLowerCase());

        Thread t = new Thread(() -> {
            for (Emoji reaction : reactions)
            {
                try
                {
                    // TODO: Krobot Request Queue
                    ((DiscordMessage) lasts[1]).getMessage().addReaction(reaction.getUnicode()).block();
                    Thread.sleep(2000L);
                }
                catch (InterruptedException | RateLimitedException e)
                {
                    Shenron.handleCommandException(caller, CommandWordReact.this, args, e);
                }
            }
        });

        t.start();
    }
}