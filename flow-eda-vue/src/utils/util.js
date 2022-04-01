//生成指定长度的唯一ID
export function generateUniqueID(length) {
    return Number(
        Math.random()
            .toString()
            .substr(3, length) + Date.now()
    ).toString(36);
}
