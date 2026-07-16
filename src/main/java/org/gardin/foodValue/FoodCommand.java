package org.gardin.foodValue;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FoodCommand implements CommandExecutor {


    private final FoodValue plugin;


    public FoodCommand(FoodValue plugin){

        this.plugin = plugin;

    }


    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ){

        if(args.length == 0){

            sender.sendMessage("§e/foodvalue reload");

            return true;

        }


        if(args[0].equalsIgnoreCase("reload")){


            if(!sender.hasPermission("foodvalue.admin")){

                sender.sendMessage("§c没有权限");

                return true;

            }


            plugin.reloadConfig();


            sender.sendMessage(
                    "§aFoodValue 配置已重载"
            );


            return true;

        }


        return false;

    }
}