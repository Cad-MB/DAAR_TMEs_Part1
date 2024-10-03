import pandas as pd
import matplotlib.pyplot as plt

# Load the CSV file into a DataFrame
df = pd.read_csv('results.csv', names=['word', 'AutomateTime', 'isThereAWordAutomate', 'KMPTime', 'isThereAWordKMP'])

# Create a figure
plt.figure(figsize=(10, 6))

# Convert 'word' to a categorical index for plotting
word_indices = range(len(df['word']))

# Ensure that the columns are converted to numpy arrays
automate_time = df['AutomateTime'].to_numpy()
kmp_time = df['KMPTime'].to_numpy()

# Scatter plot
plt.scatter(word_indices, automate_time, label='Method Automate Time (Dots)', color='blue')
plt.scatter(word_indices, kmp_time, label='Method KMP Time (Dots)', color='red')

# Line plot to connect the dots
plt.plot(word_indices, kmp_time, color='red', linestyle='-', label='Method KMP Time (Line)')
plt.plot(word_indices, automate_time, color='blue', linestyle='-', label='Method Automate Time (Line)')

# Add labels and title
plt.xlabel('Words')
plt.ylabel('Time (ms)')
plt.title('Comparison of Ahu-Ullman and KMP Execution Times')
plt.legend()
plt.grid(True)

# Set the x-axis labels to the actual words
plt.xticks(word_indices, df['word'], rotation=90)  # Rotate words on x-axis for better readability
plt.tight_layout()  # Adjust layout so that everything fits well

# Show the plot
plt.show()
