package net.grilledham.hamhacks.mixin.baritone;

import baritone.api.pathing.goals.GoalBlock;
import baritone.command.defaults.ComeCommand;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.Freecam;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ComeCommand.class)
public class MixinComeCommand {
	
	@ModifyArgs(method = "execute", at = @At(value = "INVOKE", target = "Lbaritone/api/process/ICustomGoalProcess;setGoalAndPath(Lbaritone/api/pathing/goals/Goal;)V"), remap = false)
	public void modifyGoal(Args args) {
		Freecam freecam = ModuleManager.getModule(Freecam.class);
		if(freecam.isEnabled()) {
			Vec3d pos = freecam.pos;
			args.set(0, new GoalBlock((int)pos.getX(), (int)pos.getY(), (int)pos.getZ()));
		}
	}
}
