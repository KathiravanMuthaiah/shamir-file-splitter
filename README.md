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
java -cp target/classes com.mikcore.shamir.Main input/<filename> <k> <n>


```

Example:

```bash
java -cp target/classes com.mikcore.shamir.Main input/secret.jpg 3 5
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
