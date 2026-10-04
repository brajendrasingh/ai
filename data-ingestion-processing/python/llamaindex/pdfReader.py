from llama_index.core import SimpleDirectoryReader, VectorStoreIndex

# Load PDF files from a directory
documents = SimpleDirectoryReader("./data").load_data()

# Alternatively, load a specific file
# documents = SimpleDirectoryReader(input_files=["./document.pdf"]).load_data()

# Create an index from the documents
index = VectorStoreIndex.from_documents(documents)

# Create a query engine and ask a question
query_engine = index.as_query_engine()
response = query_engine.query("What is the main topic of this document?")
print(response)