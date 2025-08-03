# 📦 Shamir File Splitter

This utility takes an input file (binary or text), base64 encodes it, chunks it based on key strength derived from threshold `k`, and applies Shamir's Secret Sharing (SSS) to generate `n` shares per chunk.

## ✅ How it Works

1. **Input**: Any file placed inside `input/`
2. **Encoding**: The file is base64 encoded and stored in `processed/`
3. **Chunking**: The base64 string is split into chunks based on `k` key size
4. **Sharing**: Each chunk is run through SSS to generate `n` shares
5. **Output**: Shares are written into folders `output/1/` to `output/n/`

## 🚀 Usage

```bash
mvn clean compile
java -cp target/classes com.mikcore.shamir.ShamirEncrypt <filename with path> <k> <n>
java -cp target/classes com.mikcore.shamir.ShamirEncrypt ./input/sampleinput.txt 256 3 5


```

Example:

```bash
java -cp target/classes com.mikcore.shamir.ShamirDecrypt <decrypt folder> <k>
java -cp target/classes com.mikcore.shamir.ShamirDecrypt ./decrypt 3
```

This will:

- Use a key strength of `256` (from `k=3`)
- Generate `5` shares per chunk with threshold `3`
- Output files in `output/1/`, `output/2/`, ..., `output/5/`

## 📂 Output Structure

```structured text
output/
├── 1/
│   ├── secret.jpg_g1_n1.txt
│   ├── secret.jpg_g2_n1.txt
│   └── ...
├── 2/
├── ...
```

## 📁 Processed Files

Base64 and chunked segments are saved in `processed/` for learning and verification.

## Sample Encryption output

*Base64 saved: processed/sampleinput.txt_base64.txt*
*Chunk size bytes: 32*
*Total Chunks : 1*
*chunkSizeBytes : 32*
*Choosing simple path with keysize:2048*
*Sectre:1396797245 processed/sampleinput.txt_g1.txt*
*Chunk 1 uses prime of 2048 bits.*
*All secure shares written to output folders.*

## Sample Decryption output

*Sectre:1396797245 decrypt/sampleinput.txt_g1.txt*
*reconstructedsampleinput.txt_chunk1:SAo=*
*✅ Reconstructed chunk: sampleinput.txt_chunk1 using 3 shares. Base64 saved: reconstructed/sampleinput.txt_chunk1.b64*

*=== Reconstruction Complete ===*
*Output File: reconstructed/reconstructed_sampleinput.txt*
*Original Hash: d98c786cff70da9d10a2c49cf9d849025d3669b95dd56cc7c27c1ebf4cbabc2c*
*Reconstructed Hash: d98c786cff70da9d10a2c49cf9d849025d3669b95dd56cc7c27c1ebf4cbabc2c*
*✅ Integrity verified successfully!*

## 🏷️ Tags
`Java` · `Cryptography` · `Shamir's Secret Sharing` · `File Splitting` · `College Project`

---

## 📜 License
This project is licensed under the [MIT License](LICENSE) – free to use for learning and educational purposes.

