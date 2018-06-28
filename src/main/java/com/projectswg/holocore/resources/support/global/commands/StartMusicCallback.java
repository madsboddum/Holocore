package com.projectswg.holocore.resources.support.global.commands;

import com.projectswg.holocore.intents.gameplay.entertainment.dance.MusicIntent;
import com.projectswg.holocore.resources.support.global.player.Player;
import com.projectswg.holocore.resources.support.objects.swg.SWGObject;
import me.joshlarson.jlcommon.log.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StartMusicCallback implements ICmdCallback {
	
	@Override
	public void execute(@NotNull Player player, @Nullable SWGObject target, @NotNull String args) {
		if (!args.isEmpty()) {
			new MusicIntent(args, player.getCreatureObject().getEquippedInstrument(), player.getCreatureObject()).broadcast();
		}
	}
}
