package net.jmecn.math;

/**
 * 四元数
 * 
 * @author yanmaoyuan
 *
 */
public class Quaternion {

    public float x, y, z, w;

    public Quaternion() {
        x = y = z = 0;
        w = 1;
    }

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }

    /**
     * 使用绕轴旋转角度来初始化四元数
     * 
     * @param axis
     * @param angle
     */
    public Quaternion(Vector3f axis, float angle) {
        axis = axis.normalize();
        
        float sinHalfAngle = (float) Math.sin(angle / 2);
        float cosHalfAngle = (float) Math.cos(angle / 2);

        this.x = axis.x * sinHalfAngle;
        this.y = axis.y * sinHalfAngle;
        this.z = axis.z * sinHalfAngle;
        this.w = cosHalfAngle;
    }

    /**
     * 设置四元数的四个分量
     * 
     * @param x
     * @param y
     * @param z
     * @param w
     * @return
     */
    public Quaternion set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    /**
     * 设置四元数的四个分量
     * 
     * @param q
     * @return
     */
    public Quaternion set(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
        return this;
    }

    /**
     * 将四元数设为 (0, 0, 0, 1)
     */
    public void loadIdentity() {
        x = y = z = 0;
        w = 1;
    }

    /**
     * 判断四元数的值是否为(0, 0, 0, 1)
     * 
     * @return true
     */
    public boolean isIdentity() {
        if (x == 0 && y == 0 && z == 0 && w == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 求负四元数
     * 
     * @return
     */
    public Quaternion negate() {
        return new Quaternion(-x, -y, -z, -w);
    }

    /**
     * 求共轭四元数
     * 
     * @return
     */
    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    /**
     * 求四元数的逆。 四元数的逆 = 四元数的共轭 / 四元数的模
     * 
     * @return
     */
    public Quaternion inverse() {
        float length = lengthSquared();
        if (length == 0f) {
            return null;
        } else if (length != 1f) {
            length = (float) (1.0 / Math.sqrt(length));
            return new Quaternion(-x * length, -y * length, -z * length, w * length);
        }
        // 单位四元数的逆和共轭是相等的。
        return conjugate();
    }

    /**
     * 求长度的平方
     * 
     * @return
     */
    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    /**
     * 求四元数的模，或者叫长度。
     * 
     * @return
     */
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    /**
     * 求单位四元数
     * 
     * @return
     */
    public Quaternion normalize() {
        float length = lengthSquared();
        if (length != 1f && length != 0f) {
            length = (float) (1.0 / Math.sqrt(length));
            return mult(length);
        }
        return new Quaternion(x, y, z, w);
    }


    /**
     * 标量乘法
     * 
     * @param scalar
     * @return
     */
    public Quaternion mult(float scalar) {
        return new Quaternion(scalar * x, scalar * y, scalar * z, scalar * w);
    }

    /**
     * 四元数乘法，或者叫叉乘。
     * 
     * @param q
     * @return
     */
    public Quaternion mult(Quaternion q) {
        float qw = q.w, qx = q.x, qy = q.y, qz = q.z;
        float rw = w * qw - x * qx - y * qy - z * qz;
        float rx = w * qx + x * qw + y * qz - z * qy;
        float ry = w * qy + y * qw + z * qx - x * qz;
        float rz = w * qz + z * qw + x * qy - y * qx;
        return new Quaternion(rx, ry, rz, rw);
    }
    
    /**
     * 计算两个四元数之间的“差”。
     * <pre>
     * 已知四元数a和b，计算从a旋转到b的角位移d。
     * a * d = b
     *     d = a的逆 * b
     * </pre>
     * @param q
     * @return
     */
    public Quaternion delta(Quaternion q) {
        return this.inverse().mult(q);
    }
    /**
     * 四元数点乘。这个值越大，说明两个四元数的旋转角度越接近。
     * 当两个四元数都是单位四元数时，返回值是它们之间夹角的余弦值。
     * 
     * @param q
     * @return
     */
    public float dot(Quaternion q) {
        return x * q.x + y * q.y + z * q.z + w * q.w;
    }

    /**
     * 四元数加法
     * @param q
     * @return
     */
    public Quaternion add(Quaternion q) {
        return new Quaternion(x + q.x, y + q.y, z + q.z, w + q.w);
    }
    
    /**
     * 四元数减法
     * @param q
     * @return
     */
    public Quaternion subtract(Quaternion q) {
        return new Quaternion(x - q.x, y - q.y, z - q.z, w - q.w);
    }
    
    /**
     * 球面线性插值(Spherical Linear intERPolation)
     *
     * @param dest
     * @param t
     */
    public Quaternion slerp(Quaternion dest, float t) {
        if (x == dest.x && y == dest.y && z == dest.z && w == dest.w) {
            return new Quaternion(this);
        }

        // 用点乘计算两四元数夹角的 cos 值
        float cos = dot(dest);

        // 如果点乘为负，则反转一个四元数以取得短的4D“弧”
        if (cos < 0.0f) {
            cos = -cos;
            dest = dest.negate();
        }

        // 计算两个四元数的插值系数
        float srcFactor = 1 - t;
        float destFactor = t;

        // 检查它们是否过于接近以避免除零
        if (cos > 0.999f) {
            // 非常接近 -- 即线性插值
            srcFactor = 1 - t;
            destFactor = t;
        } else {
            // 计算两个四元数之间的夹角
            float angle = (float) Math.acos(cos);
            // 计算分母的倒数，这样就只需要一次除法
            float invSin = 1f / (float) Math.sin(angle);

            // 计算插值系数
            srcFactor = (float) Math.sin((1 - t) * angle) * invSin;
            destFactor = (float) Math.sin((t * angle)) * invSin;
        }
        
        // 注释掉这一句，避免多次实例化对象。下面的实现效果是一样的。
        // return this.mult(srcFactor).add(dest.mult(destFactor));

        // 插值
        float rx = (srcFactor * x) + (destFactor * dest.x);
        float ry = (srcFactor * y) + (destFactor * dest.y);
        float rz = (srcFactor * z) + (destFactor * dest.z);
        float rw = (srcFactor * w) + (destFactor * dest.w);

        // 返回插值结果
        return new Quaternion(rx, ry, rz, rw);
    }
    
    
    /**
     * 使用这个四元数来旋转一个三维向量。
     * 
     * @param v
     * @return
     */
    public Vector3f mult(Vector3f v) {
        if (v.x == 0 && v.y == 0 && v.z == 0) {
            return new Vector3f(0, 0, 0);
        } else {
            float vx = v.x, vy = v.y, vz = v.z;
            float rx = w * w * vx + 2 * y * w * vz - 2 * z * w * vy + x * x * vx + 2 * y * x * vy + 2 * z * x * vz
                    - z * z * vx - y * y * vx;
            float ry = 2 * x * y * vx + y * y * vy + 2 * z * y * vz + 2 * w * z * vx - z * z * vy + w * w * vy
                    - 2 * x * w * vz - x * x * vy;
            float rz = 2 * x * z * vx + 2 * y * z * vy + z * z * vz - 2 * w * y * vx - y * y * vz + 2 * w * x * vy
                    - x * x * vz + w * w * vz;
            return new Vector3f(rx, ry, rz);
        }
    }
}