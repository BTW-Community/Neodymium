package makamys.neodymium.mixin;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

import makamys.neodymium.Neodymium;
import makamys.neodymium.ducks.ITessellator;
import makamys.neodymium.ducks.IWorldRenderer;
import makamys.neodymium.renderer.ChunkMesh;
import makamys.neodymium.renderer.NeoRenderer;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/** Inserts hooks in WorldRenderer to listen for changes, and to grab the tessellator data right before rendering. */
@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IWorldRenderer {

    @Shadow
    public boolean isInFrustum;
    @Shadow
    public boolean[] skipRenderPass;

    @Shadow
    public boolean needsUpdate;

    @Shadow private static Tessellator tessellator;
    @Shadow private int bytesDrawn;
    @Shadow private int glRenderList;

    @Shadow protected abstract void setupGLTranslation();

    @Shadow public int posX;
    @Shadow public int posY;
    @Shadow public int posZ;
    private boolean nd$savedDrawnStatus;

    private List<ChunkMesh> nd$chunkMeshes;

    @Inject(method = {"updateRenderer"}, at = @At(value = "HEAD"))
    private void preUpdateRenderer(CallbackInfo ci) {
        preUpdateRenderer(false);
    }

    /*@Inject(method = {"updateRendererSort"}, at = @At(value = "HEAD"))
    private void preUpdateRendererSort(CallbackInfo ci) {
        preUpdateRenderer(true);
    }*/

    @Unique
    private void preUpdateRenderer(boolean sort) {
        saveDrawnStatus();

        if(Neodymium.isActive()) {
            if(nd$chunkMeshes != null) {
                Collections.fill(nd$chunkMeshes, null);
            } else {
                nd$chunkMeshes = Lists.newArrayList(null, null);
            }
        }
    }

    @Inject(method = {"updateRenderer"}, at = @At(value = "RETURN"))
    private void postUpdateRenderer(CallbackInfo ci) {
        postUpdateRenderer(false);
    }

    /*@Inject(method = {"updateRendererSort"}, at = @At(value = "RETURN"))
    private void postUpdateRendererSort(CallbackInfo ci) {
        postUpdateRenderer(true);
    }*/

    @Unique
    private void postUpdateRenderer(boolean sort) {
        notifyIfDrawnStatusChanged();

        if(Neodymium.isActive()) {
            if(nd$chunkMeshes != null) {
                Neodymium.renderer.onWorldRendererPost(WorldRenderer.class.cast(this), sort);
                Collections.fill(nd$chunkMeshes, null);
            }
        }
    }

    /*@Inject(method = "preRenderBlocks", at = @At("HEAD"))
    private void prePreRenderBlocks(int pass, CallbackInfo ci) {
        if(Neodymium.isActive()) {
            ((ITessellator) Tessellator.instance).enableMeshCapturing(true);
            ChunkMesh cm = new ChunkMesh((WorldRenderer)(Object)this, pass);
            nd$chunkMeshes.set(pass, cm);
            ChunkMesh.setCaptureTarget(cm);
        }
    }*/

    @Group(min = 1, max = 1, name = "prePreRenderBlocks")
    @Inject(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glNewList(II)V", ordinal = 0, remap = false), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void prePreRenderBlocks(CallbackInfo ci, int var1, int var2, int var3, int var4, int var5, int var6, HashSet var21, byte var8, ChunkCache var9, RenderBlocks var10, int pass) {
        if(Neodymium.isActive()) {
            ((ITessellator) Tessellator.instance).enableMeshCapturing(true);
            ChunkMesh cm = new ChunkMesh((WorldRenderer)(Object)this, pass);
            nd$chunkMeshes.set(pass, cm);
            ChunkMesh.setCaptureTarget(cm);
        }
    }

    @Group(min = 1, max = 1, name = "prePreRenderBlocks")
    @Inject(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glNewList(II)V", ordinal = 0, remap = false), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private void prePreRenderBlocks1(CallbackInfo ci, int var1, int var2, int var3, int var4, int var5, int var6, HashSet var21, int var8, ChunkCache var9, RenderBlocks var10, int pass) {
        if(Neodymium.isActive()) {
            ((ITessellator) Tessellator.instance).enableMeshCapturing(true);
            ChunkMesh cm = new ChunkMesh((WorldRenderer)(Object)this, pass);
            nd$chunkMeshes.set(pass, cm);
            ChunkMesh.setCaptureTarget(cm);
        }
    }

    /*@Inject(method = "postRenderBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()I"))
    private void prePostRenderBlocks(int pass, EntityLiving entity, CallbackInfo ci) {
        /*if(Neodymium.isActive()) {
            if(nd$chunkMeshes != null) {
                if(nd$chunkMeshes.get(pass) == null) {
                    nd$chunkMeshes.set(pass, ChunkMesh.fromTessellator(pass, WorldRenderer.class.cast(this)));
                }
                nd$chunkMeshes.get(pass).addTessellatorData(Tessellator.instance);
            }
        }*/ /*
    }*/

    /*@Inject(method = "postRenderBlocks", at = @At("RETURN"))
    private void postPostRenderBlocks(int pass, EntityLiving entity, CallbackInfo ci) {
        if(Neodymium.isActive()) {
            nd$chunkMeshes.get(pass).finishConstruction();
            ((ITessellator)Tessellator.instance).enableMeshCapturing(false);
            ChunkMesh.setCaptureTarget(null);
        }
    }*/

    @Group(min = 1, max = 1, name = "postPostRenderBlocks")
    @Inject(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setTranslation(DDD)V", shift = At.Shift.AFTER, ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void postPostRenderBlocks(CallbackInfo ci, int var1, int var2, int var3, int var4, int var5, int var6, HashSet var21, byte var8, ChunkCache var9, RenderBlocks var10, int pass) {
        if(Neodymium.isActive()) {
            nd$chunkMeshes.get(pass).finishConstruction();
            ((ITessellator)Tessellator.instance).enableMeshCapturing(false);
            ChunkMesh.setCaptureTarget(null);
        }
    }

    @Group(min = 1, max = 1, name = "postPostRenderBlocks")
    @Inject(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setTranslation(DDD)V", shift = At.Shift.AFTER, ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void postPostRenderBlocks1(CallbackInfo ci, int var1, int var2, int var3, int var4, int var5, int var6, HashSet var21, int var8, ChunkCache var9, RenderBlocks var10, int pass) {
        if(Neodymium.isActive()) {
            nd$chunkMeshes.get(pass).finishConstruction();
            ((ITessellator)Tessellator.instance).enableMeshCapturing(false);
            ChunkMesh.setCaptureTarget(null);
        }
    }

    /*@Inject(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setTranslation(DDD)V", shift = At.Shift.BY, ordinal = 1, by = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    private void postPostRenderBlocks22(CallbackInfo ci, int var1, int var2, int var3, int var4, int var5, int var6, HashSet var21, byte var8, ChunkCache var9, RenderBlocks var10, int pass) {
        if(Neodymium.isActive()) {
            nd$chunkMeshes.get(pass).finishConstruction();
            ((ITessellator)Tessellator.instance).enableMeshCapturing(false);
            ChunkMesh.setCaptureTarget(null);
        }
    }*/

    @Inject(method = "setDontDraw", at = @At(value = "HEAD"))
    private void preSetDontDraw(CallbackInfo ci) {
        if(Neodymium.isActive()) {
            Neodymium.renderer.onWorldRendererChanged(WorldRenderer.class.cast(this), NeoRenderer.WorldRendererChange.DELETED);
        }
    }

    @Override
    public List<ChunkMesh> getChunkMeshes() {
        return nd$chunkMeshes;
    }

    @Inject(method = "updateInFrustum", at = @At(value = "HEAD"))
    private void preUpdateInFrustum(CallbackInfo ci) {
        saveDrawnStatus();
    }

    @Inject(method = "updateInFrustum", at = @At(value = "RETURN"))
    private void postUpdateInFrustum(CallbackInfo ci) {
        notifyIfDrawnStatusChanged();
    }

    @Unique
    private void saveDrawnStatus() {
        nd$savedDrawnStatus = isDrawn();
    }

    @Unique
    private void notifyIfDrawnStatusChanged() {
        boolean drawn = isDrawn();
        if(Neodymium.isActive() && drawn != nd$savedDrawnStatus) {
            Neodymium.renderer.onWorldRendererChanged(WorldRenderer.class.cast(this), drawn ? NeoRenderer.WorldRendererChange.VISIBLE : NeoRenderer.WorldRendererChange.INVISIBLE);
        }
    }

    @Override
    public boolean isDrawn() {
        return isInFrustum && (!skipRenderPass[0] || !skipRenderPass[1]);
    }


    /* Roughly 1.7 code, not used here */

    @Unique
    private void preRenderBlocks(int pass)
    {
        if(Neodymium.isActive()) {
            ((ITessellator) Tessellator.instance).enableMeshCapturing(true);
            ChunkMesh cm = new ChunkMesh((WorldRenderer)(Object)this, pass);
            nd$chunkMeshes.set(pass, cm);
            ChunkMesh.setCaptureTarget(cm);
        }
        /*GL11.glNewList(this.glRenderList + pass, GL11.GL_COMPILE);
        GL11.glPushMatrix();
        this.setupGLTranslation();
        float var2 = 1.000001F;
        GL11.glTranslatef(-8.0F, -8.0F, -8.0F);
        GL11.glScalef(var2, var2, var2);
        GL11.glTranslatef(8.0F, 8.0F, 8.0F);
        tessellator.startDrawingQuads();
        tessellator.setTranslation((double)(-this.posX), (double)(-this.posY), (double)(-this.posZ));*/
    }

    @Unique
    private void postRenderBlocks(int pass)
    {
        /*this.bytesDrawn += tessellator.draw();
        GL11.glPopMatrix();
        GL11.glEndList();
        tessellator.setTranslation(0.0D, 0.0D, 0.0D);*/
        if(Neodymium.isActive()) {
            nd$chunkMeshes.get(pass).finishConstruction();
            ((ITessellator)Tessellator.instance).enableMeshCapturing(false);
            ChunkMesh.setCaptureTarget(null);
        }
    }

    @Override
    @Unique
    public void updateRendererSort()
    {
        preUpdateRenderer(true);
        if (!this.skipRenderPass[1])
        {
            this.preRenderBlocks(1);
            this.postRenderBlocks(1);
        }
        postUpdateRenderer(true);
    }
}