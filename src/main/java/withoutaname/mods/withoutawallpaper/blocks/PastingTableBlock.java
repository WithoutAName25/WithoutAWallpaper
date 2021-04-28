package withoutaname.mods.withoutawallpaper.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class PastingTableBlock extends Block {
	
	public PastingTableBlock() {
		super(Properties.of(Material.WOOD)
				.strength(2.5F)
				.sound(SoundType.WOOD));
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new PastingTableTile();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	@Nonnull
	public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult trace) {
		if (!world.isClientSide) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof PastingTableTile) {
				PastingTableTile presentTile = ((PastingTableTile) tileEntity);
				presentTile.setLevelAndPosition(world, pos);
				INamedContainerProvider containerProvider = new INamedContainerProvider() {
					@Override
					public ITextComponent getDisplayName() {
						return new TranslationTextComponent("screen.withoutawallpaper.pasting_table");
					}
					
					@Override
					public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
						return new PastingTableContainer(i, world, pos, playerInventory, playerEntity);
					}
				};
				NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getBlockPos());
			} else {
				throw new IllegalStateException("No tile entity found!");
			}
		}
		return ActionResultType.SUCCESS;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
		VoxelShape shape = Block.box(0, 10, 0, 16, 11, 16);
		shape = VoxelShapes.join(shape, Block.box(1, 0, 1, 3, 10, 3), IBooleanFunction.OR);
		shape = VoxelShapes.join(shape, Block.box(1, 0, 13, 3, 10, 15), IBooleanFunction.OR);
		shape = VoxelShapes.join(shape, Block.box(13, 0, 13, 15, 10, 15), IBooleanFunction.OR);
		shape = VoxelShapes.join(shape, Block.box(13, 0, 1, 15, 10, 3), IBooleanFunction.OR);
		return shape;
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection());
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}
	
}
