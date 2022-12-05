package mff.forwardmodel.bin.core;

import mff.forwardmodel.common.SpriteTypeCommon;

public interface IBinSpriteMethods {

	float GetX(MarioBinData data, int entityIndex);

	void SetX(MarioBinData data, int entityIndex, float value);

	float GetY(MarioBinData data, int entityIndex);

	void SetY(MarioBinData data, int entityIndex, float value);

	boolean GetAlive(MarioBinData data, int entityIndex);

	void SetAlive(MarioBinData data, int entityIndex, boolean value);
//TODO: is get type needed? it can be told from sprite code
	SpriteTypeCommon GetType(MarioBinData data, int entityIndex);

	void Update(MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext);

	void CollideCheck(MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext);

	void BumpCheck(int xTile, int yTile, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext);

	boolean ShellCollideCheck(int shellEntityIndex, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext);

	boolean FireballCollideCheck(int fireballEntityIndex, MarioBinData data, int entityIndex, MarioUpdateContextBin updateContext);

	void Remove(int entityIndex, MarioBinData data);

}
