namespace API.Models
{
    public class User
    {
        public string? UserId { get; set; }
        public string? Name { get; set; }
        public string? Email { get; set; }
        public List<string>? birdSightingIds { get; set; }
        public bool? notifications { get; set; }
        public float? maxDistance { get; set; }
        public bool? metricSystem { get; set; } = true;
    }
}