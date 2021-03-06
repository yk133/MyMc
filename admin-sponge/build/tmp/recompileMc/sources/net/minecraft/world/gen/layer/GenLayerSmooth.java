package net.minecraft.world.gen.layer;

public class GenLayerSmooth extends GenLayer
{
    public GenLayerSmooth(long p_i2131_1_, GenLayer p_i2131_3_)
    {
        super(p_i2131_1_);
        super.field_75909_a = p_i2131_3_;
    }

    public int[] func_75904_a(int p_75904_1_, int p_75904_2_, int p_75904_3_, int p_75904_4_)
    {
        int i = p_75904_1_ - 1;
        int j = p_75904_2_ - 1;
        int k = p_75904_3_ + 2;
        int l = p_75904_4_ + 2;
        int[] aint = this.field_75909_a.func_75904_a(i, j, k, l);
        int[] aint1 = IntCache.func_76445_a(p_75904_3_ * p_75904_4_);

        for (int i1 = 0; i1 < p_75904_4_; ++i1)
        {
            for (int j1 = 0; j1 < p_75904_3_; ++j1)
            {
                int k1 = aint[j1 + 0 + (i1 + 1) * k];
                int l1 = aint[j1 + 2 + (i1 + 1) * k];
                int i2 = aint[j1 + 1 + (i1 + 0) * k];
                int j2 = aint[j1 + 1 + (i1 + 2) * k];
                int k2 = aint[j1 + 1 + (i1 + 1) * k];

                if (k1 == l1 && i2 == j2)
                {
                    this.func_75903_a((long)(j1 + p_75904_1_), (long)(i1 + p_75904_2_));

                    if (this.func_75902_a(2) == 0)
                    {
                        k2 = k1;
                    }
                    else
                    {
                        k2 = i2;
                    }
                }
                else
                {
                    if (k1 == l1)
                    {
                        k2 = k1;
                    }

                    if (i2 == j2)
                    {
                        k2 = i2;
                    }
                }

                aint1[j1 + i1 * p_75904_3_] = k2;
            }
        }

        return aint1;
    }
}