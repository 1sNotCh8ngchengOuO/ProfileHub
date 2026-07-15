package org.profilehub.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class InitScreen extends Screen {

    public InitScreen() {
        super(Text.literal("Profilehub"));
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("保存该客户端的 Profile"),button -> {
            //TODO: 保存该客户端的 Profile逻辑
            System.out.println("Save Profile");
        }).dimensions(this.width / 2 - 100, this.height / 2 - 10, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("加载 Profile"),button -> {
            //TODO: 加载 Profile逻辑
            System.out.println("Load Profile");
        }).dimensions(this.width / 2 - 100, this.height / 2 + 15, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("跳过"),button -> {
            this.close();
            System.out.println("Skip");
        }).dimensions(this.width / 2 - 100, this.height / 2 + 40, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderBackground(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 50, 0xFFFFFFFF);//TODO: 标题颜色更改
        super.render(context, mouseX, mouseY, deltaTicks);
    }
}