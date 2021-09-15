package graphics.kiln.rosella.render.material;

import graphics.kiln.rosella.render.pipeline.Pipeline;
import graphics.kiln.rosella.render.texture.TextureMap;

/**
 * A Material has a pipeline and any attributes that aren't pipeline specific or instance specific.
 * For example, a material has a {@link TextureMap} because many instances may use the same textures,
 * but a pipeline doesn't require textures to be created.
 */
public record Material(Pipeline pipeline, TextureMap textures) {
}

