package org.jglrxavpok.mods.customai.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.common.CustomAIHelper;
import org.jglrxavpok.mods.customai.json.JSONObject;
import org.jglrxavpok.mods.customai.netty.PacketUpdateAI;
import org.lwjgl.opengl.GL11;

public class GuiCustomAI extends GuiScreen
{

    public static final ResourceLocation backgroundTexture = new ResourceLocation(ModCustomAI.MODID, "textures/gui/ai_rewriter_gui.png");
    public static final ResourceLocation secondPartBackgroundTexture = new ResourceLocation(ModCustomAI.MODID, "textures/gui/ai_rewriter_gui_part2.png");
    public static final ResourceLocation backgroundTexture2 = new ResourceLocation(ModCustomAI.MODID, "textures/gui/ai_rewriter_gui2.png");
    public static final ResourceLocation backgroundTexture3 = new ResourceLocation(ModCustomAI.MODID, "textures/gui/ai_rewriter_gui3.png");
    private EntityPlayer player;
    private World worldObj;
    
    private Entity entity;
    
    protected int xSize = 176;
    /**
     * The Y size of the inventory window in pixels.
     */
    protected int ySize = 166;
    private GuiAIChoiceButton targetTasksButton;
    private GuiAIChoiceButton tasksButton;
    
    private HashMap<String, ArrayList<GuiButton>> parts = new HashMap<String, ArrayList<GuiButton>>();
    private ArrayList<GuiButton> secondPart = new ArrayList<GuiButton>();
    private String currentPart;
    private GuiButton selectedButton;
    private GuiList targetList;
    private List<EntityAITaskEntry> targetTasks;
    private GuiList tasksList;
    private List<EntityAITaskEntry> tasks;
    private float currentTranslation;
    private int aimedTranslation;
    private EntityAITaskEntry currentEntry;
    private JSONObject entryData;
    private ArrayList<GuiTextField2> secondPartFields = new ArrayList<GuiTextField2>();
    private int currentEntryIndex;
    private boolean isTarget;

    public GuiCustomAI(EntityPlayer player, World world, Entity entity)
    {
        this.player = player;
        this.worldObj = world;
        this.entity = entity;
        currentPart = "Main";
        targetTasks = CustomAIHelper.getTargetTasksList(entity);
        tasks = CustomAIHelper.getTasksList(entity);
    }
    
    @SuppressWarnings("unchecked")
    public void displayPart(String partName)
    {
        this.buttonList.clear();
        ArrayList<GuiButton> buttons = parts.get(partName);
        currentPart = partName;
        for(GuiButton button : buttons)
        {
            buttonList.add(button);
        }
    }
    
    public void initGui()
    {
        buttonList.clear();
        parts.clear();
        ArrayList<GuiButton> mainPart = new ArrayList<GuiButton>();
        GuiButton confirm = new GuiButton(0, width/2-80, height/2+50, 75,20,"Confirm");
        GuiButton cancel = new GuiButton(1, width/2+5,height/2+50,75,20,"Cancel");
        
        targetTasksButton = new GuiAIChoiceButton(3, width/2-80,height/2,160,30,"Rewrite target tasks");
        tasksButton = new GuiAIChoiceButton(4, width/2-80,height/2-40,160,30,"Rewrite tasks");
        
        mainPart.add(targetTasksButton);
        mainPart.add(tasksButton);
        mainPart.add(confirm);
        mainPart.add(cancel);
        parts.put("Main",mainPart);
        this.displayPart("Main");
        
        
        buildTargetTasksPart();
        buildTasksPart();
        
        
        for(ArrayList<GuiButton> list : parts.values())
        {
            if(list != mainPart)
                list.add(new GuiButton(2, width/2-75/2,height/2+50,75,20,"Back"));
        }
        
    }
    
    private void buildTargetTasksPart()
    {
        ArrayList<GuiButton> targetPart = new ArrayList<GuiButton>();
        GuiListSlot[] slots = new GuiListSlot[targetTasks.size()+1];
        for(int i = 0;i<slots.length-1;i++)
        {
            slots[i] = new GuiIAListSlot(i+100,targetTasks.get(i));
        }
        slots[slots.length-1] = new GuiAddToListSlot(-20);
        ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth,mc.displayHeight);
        int width1 = res.getScaledWidth();
        int height1 = res.getScaledHeight();
        targetList = new GuiList(slots,width1/2-75,height1/2-55,150,100,20);
        targetList.setOutBoundsMethod(1);
        parts.put("Target tasks",targetPart);
    }
    
    private void buildTasksPart()
    {
        ArrayList<GuiButton> tasksPart = new ArrayList<GuiButton>();
        GuiListSlot[] slots = new GuiListSlot[tasks.size()+1];
        for(int i = 0;i<slots.length-1;i++)
        {
            slots[i] = new GuiIAListSlot(i+100,tasks.get(i));
        }
        slots[slots.length-1] = new GuiAddToListSlot(-20);
        ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth,mc.displayHeight);
        int width1 = res.getScaledWidth();
        int height1 = res.getScaledHeight();
        tasksList = new GuiList(slots,width1/2-75,height1/2-55,150,100,20);
        tasksList.setOutBoundsMethod(1);
        parts.put("Tasks",tasksPart);
    }

    public void translate(float currentTranslation2)
    {
        tasksList.xPos+=currentTranslation2;
        targetList.xPos+=currentTranslation2;
        ArrayList<GuiButton> alreadyDone = new ArrayList<GuiButton>();
        for(ArrayList<GuiButton> list : parts.values())
        {
            for(GuiButton button : list)
            {
                if(!alreadyDone.contains(button))
                {
                    button.xPosition+=currentTranslation2;
                    alreadyDone.add(button);
                }
            }
        }
     
        for(GuiButton button : secondPart)
        {
            if(!alreadyDone.contains(button))
            {
                button.xPosition-=currentTranslation2;
                alreadyDone.add(button);
            }
        }
        
        for(GuiTextField2 field : secondPartFields)
        {
            field.addToX(-currentTranslation2);
        }
    }
    
    public void updateScreen()
    {
        targetTasksButton.setListSize(targetTasks.size());
        tasksButton.setListSize(tasks.size());
        if(aimedTranslation > currentTranslation)
            currentTranslation += xSize/15;
        if(aimedTranslation < currentTranslation)
            currentTranslation -= xSize/15;
        for(GuiTextField field : secondPartFields)
        {
            field.updateCursorCounter();
        }
        
    }
    
    protected void keyTyped(char par1, int par2)
    {
        for(GuiTextField field : secondPartFields)
        {
            if(field.isFocused())
                field.textboxKeyTyped(par1, par2);
        }
    }
    
    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        if (button == 0)
        {
            if(currentPart.equals("Target tasks"))
            {
                selectedButton = targetList.handleClick(mc, (int) (mouseX),mouseY);
            }
            else if(currentPart.equals("Tasks"))
            {
                selectedButton = tasksList.handleClick(mc, (int) (mouseX),mouseY);
                
            }
            for (int l = 0; l < this.secondPart.size(); ++l)
            {
                GuiButton guibutton = (GuiButton)this.secondPart.get(l);

                if (guibutton.mousePressed(this.mc, (int) (mouseX+currentTranslation), mouseY))
                {
                    this.selectedButton = guibutton;
                }
            }
            for (int l = 0; l < this.buttonList.size(); ++l)
            {
                GuiButton guibutton = (GuiButton)this.buttonList.get(l);

                if (guibutton.mousePressed(this.mc, (int) (mouseX-currentTranslation), mouseY))
                {
                    this.selectedButton = guibutton;
                }
            }
        }
    }
    
    protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        if (this.selectedButton != null && p_146286_3_ == 0)
        {
            this.selectedButton.mouseReleased((int) (p_146286_1_-currentTranslation), p_146286_2_);
            selectedButton.func_146113_a(this.mc.getSoundHandler());
            actionPerformed(selectedButton);
            this.selectedButton = null;
        }
        
        for(GuiTextField field : secondPartFields)
        {
            field.mouseClicked((int) (p_146286_1_+currentTranslation), p_146286_2_, p_146286_3_);
        }
    }
    
    public void actionPerformed(GuiButton button)
    {
        if(button.id == 0)
        {
            if(currentEntry != null)
            {
                for(GuiTextField2 field : secondPartFields)
                {
                    entryData.put(field.getValueName(),field.getText());
                    System.out.println("Set "+field.getValueName()+" to "+field.getText());
                }
                if(isTarget)
                {
                    targetTasks.remove(currentEntry);
                }
                else
                    tasks.remove(currentEntry);
                this.currentEntry = CustomAIHelper.generateAIFromJSON(entity, entryData.toString());
                if(isTarget)
                {
                    targetTasks.add(currentEntryIndex, currentEntry);
                }
                else
                    tasks.add(currentEntryIndex, currentEntry);
            }
            ModCustomAI.packetPipeline.sendToServer(new PacketUpdateAI(entity.getEntityId(), tasks, targetTasks));
            FMLNetworkHandler.openGui(player, ModCustomAI.instance, -1, worldObj, 0, 0, 0);
        }
        else if(button.id == 1)
        {
            FMLNetworkHandler.openGui(player, ModCustomAI.instance, -1, worldObj, 0, 0, 0);
        }
        else if(button.id == 2)
        {
            this.displayPart("Main");
        }
        else if(button.id == -202 && button instanceof GuiCheckBox)
        {
            ((GuiCheckBox)button).handleClicked();
        }
        if(this.currentPart.equals("Main"))
        {
            if(button.id == 3)
            {
                this.displayPart("Target tasks");
            }
            else if(button.id == 4)
            {
                this.displayPart("Tasks");
            }
        }
        else if(currentPart.equals("Tasks"))
        {
            if(button.id >= 100)
            {
                if(currentEntry != null)
                {
                    for(GuiTextField2 field : secondPartFields)
                    {
                        entryData.put(field.getValueName(),field.getText());
                        System.out.println("Set "+field.getValueName()+" to "+field.getText());
                    }
                    if(isTarget)
                    {
                        targetTasks.remove(currentEntry);
                    }
                    else
                        tasks.remove(currentEntry);
                    this.currentEntry = CustomAIHelper.generateAIFromJSON(entity, entryData.toString());
                    if(isTarget)
                    {
                        targetTasks.add(currentEntryIndex, currentEntry);
                    }
                    else
                        tasks.add(currentEntryIndex, currentEntry);
                }
                EntityAITaskEntry entry = this.tasks.get(button.id-100);
                currentEntryIndex = button.id-100;
                this.currentEntry = entry;
                isTarget = false;
                buildSecondPart(entry);
            }
            else if(button.id == -20)
            {
                // Add a new task
            }
            else if(button.id == -201)
            {
                if(isTarget)
                {
                    targetTasks.remove(currentEntry);
                }
                else
                    tasks.remove(currentEntry);
                currentEntry = null;
                entryData = null;
                initGui();
                this.buildSecondPart(null);
                this.displayPart(isTarget ? "Target tasks" : "Tasks");
            }
            else if(button.id == -200 && button instanceof GuiSpinnerButton)
            {
                ((GuiSpinnerButton)button).handleClicked();
            }
        }
        else if(currentPart.equals("Target tasks"))
        {
            if(button.id >= 100)
            {
                if(currentEntry != null)
                {
                    for(GuiTextField2 field : secondPartFields)
                    {
                        entryData.put(field.getValueName(),field.getText());
                        System.out.println("Set "+field.getValueName()+" to "+field.getText());
                    }
                    if(isTarget)
                    {
                        targetTasks.remove(currentEntry);
                    }
                    else
                        tasks.remove(currentEntry);
                    this.currentEntry = CustomAIHelper.generateAIFromJSON(entity, entryData.toString());
                    if(isTarget)
                    {
                        targetTasks.add(currentEntryIndex, currentEntry);
                    }
                    else
                        tasks.add(currentEntryIndex, currentEntry);
                }
                EntityAITaskEntry entry = this.targetTasks.get(button.id-100);
                currentEntryIndex = button.id-100;
                this.currentEntry = entry;
                isTarget = true;
                buildSecondPart(entry);
            }
            else if(button.id == -20)
            {
                
            }
            else if(button.id == -201)
            {
                if(isTarget)
                {
                    targetTasks.remove(currentEntry);
                }
                else
                    tasks.remove(currentEntry);
                currentEntry = null;
                entryData = null;
                initGui();
                this.buildSecondPart(null);
                this.displayPart(isTarget ? "Target tasks" : "Tasks");
            }
            else if(button.id == -200 && button instanceof GuiSpinnerButton)
            {
                ((GuiSpinnerButton)button).handleClicked();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void buildSecondPart(EntityAITaskEntry entry)
    {
        this.secondPart.clear();
        this.secondPartFields.clear();
        if(entry == null)
            return;
        ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth,mc.displayHeight);
        int width1 = res.getScaledWidth();
        int height1 = res.getScaledHeight();
        try
        {
            entryData = CustomAIHelper.generateJSONFromAI((EntityLiving) entity, entry);
            secondPart.add(new GuiLabel(width1/2+50,height1/2-60,"0"));
            secondPart.add(new GuiLabel(width1/2-75,height1/2-60,"Priority:"));
            secondPart.get(0).displayString = ""+entryData.getInt("priority");
            addSpinnerButtons(entryData,"priority",secondPart.get(0), width1/2+70,height1/2-70,0,100,Integer.class,1);
            
            Iterator<String> it = entryData.keys();
            int y = 20;
            while(it.hasNext())
            {
                String key = it.next();
                Object value = entryData.get(key);
                if(value == null || key.equals("priority") || key.equals("type"))
                    continue;
                GuiLabel label = new GuiLabel(width1/2-75,height1/2-60+y,key+":");
                secondPart.add(label);
                GuiLabel valueLabel = new GuiLabel(width1/2+50,height1/2-60+y,""+value);
                if(value instanceof String)
                {
                    y+=2;
                    GuiTextField2 f = new GuiTextField2(mc.fontRenderer,width1/2+30,height1/2-60+y-10,50,20);
                    f.setMaxStringLength(Integer.MAX_VALUE);
                    f.setText((String)value);
                    f.setValueName(key);
                    secondPartFields.add(f);
                    y+=2;
                }
                else if(value instanceof Integer)
                {
                    addSpinnerButtons(entryData,key,valueLabel,width1/2+70,height1/2-70+y,0,1000,Integer.class, 1);
                    secondPart.add(valueLabel);
                }
                else if(value instanceof Float)
                {
                    addSpinnerButtons(entryData,key,valueLabel,width1/2+70,height1/2-70+y,0,1000,Float.class, 0.1f);
                    secondPart.add(valueLabel);
                }
                else if(value instanceof Double)
                {
                    addSpinnerButtons(entryData,key,valueLabel,width1/2+70,height1/2-70+y,0,1000,Double.class, 0.1);
                    secondPart.add(valueLabel);
                }
                else if(value instanceof Boolean)
                {
                    addCheckBox(entryData,key,width1/2+60,height1/2-70+y);
                }
                y+=20;
            }
//            y+=3;
            GuiButton removeTask = new GuiButton(-201, width1/2-75,height1/2-70+y,xSize-20,20,"Remove task");
            this.secondPart.add(removeTask);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addCheckBox(JSONObject entryData2, String key, int x, int y)
    {
        secondPart.add(new GuiCheckBox(entryData2,key,x,y));
    }

    private void addSpinnerButtons(JSONObject data, String string, GuiButton guiButton, int x, int y, double min, double max, Class<?> valueType, double step)
    {
        secondPart.add(new GuiSpinnerButton(data,string,guiButton,x,y+2,valueType, step, true).setValueBounds(min,max));
        secondPart.add(new GuiSpinnerButton(data,string,guiButton,x,y+10,valueType, step, false).setValueBounds(min,max));
    }

    public void drawScreen(int par1, int par2, float par3)
    {
        if(currentPart.equals("Main"))
        {
            setTranslationAimed(0);
        }
        else
        {
            setTranslationAimed(-xSize/2-10);
        }
        translate(currentTranslation);
        drawSecondPart(par1, par2);
        GL11.glDisable(GL11.GL_LIGHTING);

        this.drawGuiContainerBackgroundLayer(par3, par1, par2);
        if(currentPart.equals("Target tasks"))
            this.targetList.render(mc, par1, par2);
        else if(currentPart.equals("Tasks"))
            this.tasksList.render(mc, par1, par2);
        
        if(!this.currentPart.equals("Main"))
        {
            GL11.glColor4f(1, 1,1,1);

            this.mc.renderEngine.bindTexture(backgroundTexture2);
            int k = (int) (width/2-xSize/2+currentTranslation);
            int l = height/2-ySize/2-5;
            this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        }
        GL11.glColor4f(1, 1,1,1);

        super.drawScreen(par1, par2, par3);
        if(currentPart.equals("Target tasks"))
        {
            this.targetList.postRender(mc, par1, par2);
        }
        else if(currentPart.equals("Tasks"))
        {
            this.tasksList.postRender(mc, par1, par2);
        }
        this.drawGuiContainerForegroundLayer(par1, par2);
        
        translate(-currentTranslation);
        GL11.glEnable(GL11.GL_LIGHTING);

    }

    @SuppressWarnings("unused")
    private void drawSecondPart(int mx, int my)
    {
        if((int)currentTranslation == 0 && currentPart.equals("Main"))
            return;
        background :
        {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.renderEngine.bindTexture(secondPartBackgroundTexture);
            int k = (int) (width/2-xSize/2-(currentTranslation));
            int l = height/2-ySize/2-5;
            this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
            GL11.glPopMatrix();
        }
        middle:
        {
            for(GuiButton button : secondPart)
            {
                button.drawButton(mc, mx, my);
            }
            for(GuiTextField field : secondPartFields)
            {
                field.drawTextBox();
            }
        }
        
        foreground:
        {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPushMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            int x = width/2;
            int y = height/2-ySize/2;
            String s1 = "Waiting for task";
            if(currentEntry != null)
                s1 = CustomAIHelper.getNameForTask(this.currentEntry.action);
            fontRendererObj.drawStringWithShadow(s1, (int) (x-fontRendererObj.getStringWidth(s1)/2-currentTranslation), y, 0xFFFFFF);
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

    private void setTranslationAimed(int i)
    {
        aimedTranslation = i;
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int x = width/2;
        int y = height/2-ySize/2;
        String s1 = "AI Rewriter - "+this.currentPart;
        if(currentPart.equals("Target tasks"))
            s1+=" ("+this.targetTasks.size()+")";
        if(currentPart.equals("Tasks"))
            s1+=" ("+this.tasks.size()+")";
        fontRendererObj.drawStringWithShadow(s1, (int) (x-fontRendererObj.getStringWidth(s1)/2+currentTranslation), y, 0xFFFFFF);
        String s = "Editing AI of "+entity.getCommandSenderName()+"(ID:"+this.entity.getEntityId()+")";
        fontRendererObj.drawStringWithShadow(s, (int) (x-fontRendererObj.getStringWidth(s)/2+1+currentTranslation), y+15, 0xFFFFFF);
        GL11.glEnable(GL11.GL_LIGHTING);
    }
    
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        if(this.currentPart.equals("Main"))
        {
            this.mc.renderEngine.bindTexture(backgroundTexture);
            int k = (int) (width/2-xSize/2+currentTranslation);
            int l = height/2-ySize/2-5;
            this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        }
        else
        {
            this.mc.renderEngine.bindTexture(backgroundTexture3);
            int k = (int) (width/2-xSize/2+currentTranslation);
            int l = height/2-ySize/2-5;
            this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        }
        GL11.glPopMatrix();
    }

}
