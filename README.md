# 简介
为了鼓励玩家尽量不完全依赖原版食物，尽可能制作我使用craftEngine提供的高级食物，于是我制作了这个插件
用于修改原版食物的饱食度和饱和度，并支持独立的蛋糕配置、额外药水效果和自定义命令。
当前依赖适配的 CraftEngine 版本为 `26.8`。
## 用法
因为我使用1.21.11的paper端，所以这个插件自然是为此服务的，我已经在配置文件中默认添加了所有可食用的食物
的默认配置并按照维基百科的顺序分好了类别
当你第一次成功启动服务端后，生成的配置文件会如下图所示
```yaml
debug: true
foods:
  minecraft:apple:
    nutrition: 2
    saturation: 0.3
    effects: []
    commands: []
cakes:
  minecraft:cake:
    nutrition: 2
    saturation: 0.4
    effects: []
    commands: []
  ......
```
普通食物应该在 `foods` 下编辑，蛋糕应该在 `cakes` 下编辑，更详细的信息可以在配置文件中查看
CraftEngine 食物也可以写在 `foods` 下，格式使用 `"CE:namespace:item_id"`。

## 新增能力
- CraftEngine 自定义食物会先检查 `foods` 里的 CE 配置；有配置就接管，没有配置才跳过
- 蛋糕现在使用独立的 `cakes` 配置节点，支持自定义饱食度、饱和度、效果和命令
- `effects` 字段可追加药水效果
- `commands` 字段可在食用后执行命令，支持 `[console]` 和 `[player]` 前缀
- 命令支持内置占位符：`{player}`、`{player_name}`、`{player_uuid}`、`{food_id}`
- 如果服务器安装了 PlaceholderAPI，命令里也可以直接使用 PAPI 占位符

## effects 示例
```yaml
foods:
  minecraft:apple:
    nutrition: 2
    saturation: 0.3
    effects:
      - type: regeneration
        duration: 100
        amplifier: 0
```

## CraftEngine 食物示例
```yaml
foods:
  "CE:myfood:cheese_burger":
    nutrition: 6
    saturation: 3.0
    effects: []
    commands:
      - "[console] say {player} 吃下了 CE 食物 {food_id}"
```

## commands 示例
```yaml
foods:
  minecraft:apple:
    nutrition: 2
    saturation: 0.3
    commands:
      - "[console] say {player} 吃下了 {food_id}"
      - "[player] msg {player} 苹果味道不错"
```

debug模式还非常简陋，主要用于查看物品名和修改状态
